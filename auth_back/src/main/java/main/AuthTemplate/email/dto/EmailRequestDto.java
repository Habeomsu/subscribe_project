package main.AuthTemplate.email.dto;

import lombok.Getter;

public class EmailRequestDto {

    @Getter
    public static class EmailDto{

        private String email;
    }

    @Getter
    public static class EmailCheckDto{

        private String email;
        private String authNum;
    }
}
