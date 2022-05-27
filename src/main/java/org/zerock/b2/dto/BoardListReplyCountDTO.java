package org.zerock.b2.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListReplyCountDTO {

    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;
    private long replyCount; //기본 값을 0으로 가지도록 하기 위해서 int 값으로 했으나
                            //사실은 long 을 쓰는게 맞다. jpa 에서 처리할 때는 long 값으로 리턴되기 때문에


}
