package org.zerock.b2.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table(name = "t_board") //테이블에 생성되는 이름은 다르게 할래
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString(exclude = "boardImages")
public class Board extends BaseEntity{
                     // ↓ 전략
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    //  ↑ 이건 생성되는 value 값이야
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;
// 제네릭은 기본타입을 못씀. pk는 반드시 객체 인 Integer 로!

    // 컬럼 기본은 255  ↓ 길이   ↓ not null 의미함
    @Column(length = 200, nullable = false)
    private String title;
    private String content;
    private String writer;

/*    @CreationTimestamp //이건 하이버에 특화된 기능
      private LocalDateTime regDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;*/
    // BaseEntity 에 들어가므로 지운다.

    //오늘 보다 내일 쓰는 방법을 더 많이 씀.

    //수정
    public void changeTitle(String title){
        this. title = title;
    }
    public void changContent(String content){
        this.content = content;
    }

    //첨부파일
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) // OneToMany 는 첨부파일을 추가하는데 보드를 건드려야 하는 구조...
    @JoinColumn(name = "board")
    @Builder.Default
    //@BatchSize(size = 100)
    private Set<BoardImage> boardImages = new HashSet<>();
    //자료구조를 반환할 때는 null 을 체크 안한다...(?)

    public void addImage(BoardImage boardImage){
        boardImage.fixOrd(boardImages.size());
        boardImages.add(boardImage);

    }
}
