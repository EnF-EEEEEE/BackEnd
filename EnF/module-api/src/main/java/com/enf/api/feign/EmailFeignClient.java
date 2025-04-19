package com.enf.api.feign;


import com.enf.api.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "emailClient",
        url = "http://module-email:8082", //"http://localhost:8082",
        configuration = FeignClientConfig.class
)
public interface EmailFeignClient {

    /**
     * 이메일 전송 요청을 보내는 메서드
     *
     * @param to      수신자 이메일 주소
     * @return 이메일 전송 결과
     */
    @PostMapping("/api/v1/email/send/mentee-letter-arrived")
    void sendMenteeLetterArrivedEmail(@RequestParam("email") String to, @RequestParam("nickname") String nickname);

    @PostMapping("/api/v1/email/send/mentor-letter-arrived")
    void sendMentorLetterArrivedEmail(@RequestParam("email") String to, @RequestParam("nickname") String nickname);

    @PostMapping("/api/v1/email/send/one-day-notice")
    void sendOneDayNoticeEmail(@RequestParam("email") String to, @RequestParam("nickname") String nickname);
}