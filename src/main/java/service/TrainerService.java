package service;

import repository.TrainerRepository;

/**
 * 로그인, 회원 예약 일정 변경 등 구현
 */
public class TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }


}