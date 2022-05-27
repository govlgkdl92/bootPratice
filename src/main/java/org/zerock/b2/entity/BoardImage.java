package org.zerock.b2.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "t_board_image")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    private String uuid;

    private String fileName;

    private boolean img;

    private int ord; //첨부파일의 순번을 잡아준다.

    public void fixOrd(int ord){
        this.ord = ord;
    }
}
