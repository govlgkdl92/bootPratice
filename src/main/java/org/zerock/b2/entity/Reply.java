package org.zerock.b2.entity;

import lombok.*;

import javax.persistence.*;

@Table(name = "t_reply",
        indexes = { @Index(name = "idx_reply_board_bno", columnList = "board_bno")})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString(exclude = "board") //ToString 할 때 board는 빼줘~
public class Reply extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto increment 를 쓸 땐 identity
    private Long rno;

    private String replyText;
    private String replier;

    // ** 연관관계를 주면 가능한 Lazy 를 줘라! - 그리고 이 연관관계는 ToString 에서 제외시켜라! **
    // 컬럼명 지정하면 다른 이름의 컬럼을 쓸 수도 있다
                                //레이지- 최대한 미루는 것 - 성능의 향상을 위해 하는 경우가 많다.
    @ManyToOne(fetch = FetchType.LAZY) //내가 필요할 때까지 로딩을 하지 않는다. 내가 필요한 순간까지 기다렸다가 select 를 하겠다.
    private Board board; //게시글 테이블... // ToString 을 할 때는 board 객체가 필요해!
                                        // 그 때가서 select 를 하려고 하니까 database 랑 연결된 게 없는데 ? no Session 에러가 난다.
    
    //수정
    public void changeText(String text){
        this.replyText = text;
    }

}
