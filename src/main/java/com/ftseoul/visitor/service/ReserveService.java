package com.ftseoul.visitor.service;

import com.ftseoul.visitor.data.Reserve;
import com.ftseoul.visitor.data.ReserveRepository;
import com.ftseoul.visitor.data.StaffRepository;
import com.ftseoul.visitor.data.Visitor;
import com.ftseoul.visitor.data.VisitorRepository;
import com.ftseoul.visitor.dto.reserve.ReserveRequestDto;
import com.ftseoul.visitor.dto.reserve.ReserveListResponseDto;
import com.ftseoul.visitor.dto.reserve.ReserveModifyDto;
import com.ftseoul.visitor.dto.reserve.ReserveVisitorDto;
import com.ftseoul.visitor.dto.visitor.VisitorDecryptDto;
import com.ftseoul.visitor.dto.visitor.VisitorDto;
import com.ftseoul.visitor.dto.payload.Response;
import com.ftseoul.visitor.encrypt.Seed;
import com.ftseoul.visitor.exception.error.PhoneDuplicatedException;
import com.ftseoul.visitor.exception.error.ResourceNotFoundException;
import com.ftseoul.visitor.websocket.WebSocketService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final VisitorRepository visitorRepository;
    private final StaffService staffService;
    private final StaffRepository staffRepository;
    private final Seed seed;
    private final WebSocketService socketService;

    public ReserveListResponseDto findById(Long id) {
        Reserve reserve = reserveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserve", "id", id));
        List<VisitorDecryptDto> visitor = visitorRepository.findAllByReserveId(id)
                .stream().map(v -> VisitorDecryptDto.builder()
                .reserveId(v.getReserveId())
                .phone(v.getPhone())
                .name(v.getName())
                .organization(v.getOrganization())
                .build().decryptDto(seed)).collect(Collectors.toList());
        return ReserveListResponseDto.builder()
                .staff(staffService.decryptStaff(staffRepository.findById(reserve.getTargetStaff())
                        .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", reserve.getTargetStaff()))))
                .place(reserve.getPlace())
                .date(reserve.getDate())
                .id(reserve.getId())
                .purpose(reserve.getPurpose())
                .visitor(visitor)
                .build();
    }

    public List<ReserveListResponseDto> findReservesByNameAndPhone(ReserveRequestDto requestDto) {
        log.info("Search reserve lists by name and phone\nname: {}, phone: {}", seed.encrypt(requestDto.getName()), seed.encrypt(requestDto.getPhone()));
        List<Visitor> visitorList = visitorRepository.findAllByNameAndPhone(seed.encrypt(requestDto.getName()), seed.encrypt(requestDto.getPhone()));
        List<ReserveListResponseDto> response = new ArrayList<>();
        for (int i = 0; i < visitorList.size(); i++) {
            int finalI = i;
            Reserve reserve = reserveRepository.findById(visitorList.get(i).getReserveId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reserve", "id", visitorList.get(finalI).getReserveId()));
            List<VisitorDecryptDto> visitors = visitorRepository.findAllByReserveId(reserve.getId())
                    .stream().map(v -> VisitorDecryptDto.builder()
                            .reserveId(v.getReserveId())
                            .name(v.getName())
                            .phone(v.getPhone())
                            .organization(v.getOrganization())
                            .build().decryptDto(seed)).collect(Collectors.toList());
            response
                    .add(ReserveListResponseDto.builder()
                            .id(reserve.getId())
                            .date(reserve.getDate())
                            .place(reserve.getPlace())
                            .purpose(reserve.getPurpose())
                            .staff(staffService.decryptStaff(staffRepository.findById(reserve.getTargetStaff())
                                    .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", reserve.getTargetStaff()))))
                            .visitor(visitors)
                            .build());
        }
        return response;
    }

    private boolean deleteVisitorInList(List<Visitor> list, ReserveRequestDto requestDto) {
        Optional<Visitor> toDeleteVisitor = list.stream().filter( visitor ->
                (visitor.getName().equals(seed.encrypt(requestDto.getName())))
                    && (visitor.getPhone().equals(seed.encrypt(requestDto.getPhone()))))
            .findAny();
        if (toDeleteVisitor.isEmpty()) {
            log.error("????????? ????????? ???????????? ???????????? ??????????????? ???????????? ????????????");
            return false;
        }
        visitorRepository.delete(toDeleteVisitor.get());
        log.info("Visitor delete: " + toDeleteVisitor.get());
        return true;
    }

    public boolean visitorReserveDelete(Long reserveId, ReserveRequestDto requestDto) {
        log.info("Delete Reserve Id: {}", reserveId);
        log.info("Delete Visitors: {}", requestDto);
        List<Visitor> list = visitorRepository.findAllByReserveId(reserveId);
        if (list.isEmpty()) {
            log.error("???????????? {}??? ???????????? ???????????? ???????????? ????????????", reserveId.toString());
            return false;
        }

        boolean result = deleteVisitorInList(list, requestDto);

        if (list.size() == 1) {
            log.info("Reserve delete: " + reserveId);
            reserveRepository.delete(reserveRepository.findById(reserveId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserve", "reserveId", reserveId)));
            socketService.sendMessageToSubscriber("/visitor",
                "?????? ??????: "+ reserveId + " ?????? ??? ???????????? ?????????????????????");
        } else {
            socketService.sendMessageToSubscriber("/visitor", "?????? ??????: "+ reserveId + " ??? ???????????? ???????????? ??????????????????");
        }
        return result;
    }

    public Reserve saveReserve(ReserveVisitorDto reserveVisitorDto, long staffId){
        checkDuplicatedPhone(reserveVisitorDto.getVisitor());
        Reserve reserve = reserveRepository.save(Reserve.builder()
                .targetStaff(staffId)
                .place(reserveVisitorDto.getPlace())
                .purpose(reserveVisitorDto.getPurpose())
                .date(reserveVisitorDto.getDate())
                .build());
        log.info("Reserve Saved: {}", reserve);
        return reserve;
    }

    public Reserve updateReserve(ReserveModifyDto reserveModifyDto, long staffId) {
        Reserve reserve = reserveRepository
            .findById(reserveModifyDto.getReserveId())
            .orElseThrow(() -> new ResourceNotFoundException("Reserve", "id", reserveModifyDto.getReserveId()));
        reserve.update(reserveModifyDto.getPlace(), staffId,
            reserveModifyDto.getPurpose(), reserveModifyDto.getDate());
        log.info("Updated reserve: {}", reserve);
        reserveRepository.save(reserve);
        return reserve;
    }

    public void checkDuplicatedPhone(List<VisitorDto> visitorDto) {
        log.info("Check phone Duplication");
        Set<String> phones = new HashSet<>();
        List<VisitorDto> collected = visitorDto
            .stream()
            .filter(visitor -> !phones.add(visitor.getPhone()))
            .collect(Collectors.toList());
        if (!collected.isEmpty()) {
            throw new PhoneDuplicatedException("???????????? ??????");
        }
    }

    public Response deleteById(Long id) {
        Optional<Reserve> reserve = reserveRepository.findById(id);
        if (reserve.isEmpty()) {
            return new Response("4000", "??????????????? ???????????? ????????????");
        }
        log.info("id: {} ??? ???????????? ????????? ???????????????", id);
        reserveRepository.deleteById(id);
        visitorRepository.deleteAllByReserveId(id);
        return new Response("2000", "????????? ?????????????????????");
    }

    public void deleteAllByStaffId(Long id) {
        log.info("????????? id: {}??? ???????????? ?????? ??? ????????? ???????????? ???????????????", id);
        List<Reserve> reserveList = reserveRepository.findAllByTargetStaff(id);
        if (reserveList != null && !reserveList.isEmpty()) {
            reserveList.forEach(reserve -> {
                long reserveId = reserve.getId();
                visitorRepository.deleteAllByReserveId(reserveId);
                reserveRepository.delete(reserve);
            });
        }
    }

    public Long findStaffByReserveId(Long reserveId) {
        Optional<Reserve> reserve = reserveRepository.findById(reserveId);
        return reserve.get().getTargetStaff();
    }
}
