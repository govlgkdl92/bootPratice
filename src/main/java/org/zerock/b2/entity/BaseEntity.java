package org.zerock.b2.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass         // 뭔가를 변경하면 예가 동작함 
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
public class BaseEntity {
    // board 클래스 는 상속을 뺄 것임

    @CreatedDate            // 생성할 때만 들어가고 갱신은 못한다는 뜻 updatable = false
    @Column(name = "regDate", updatable = false)
    private LocalDateTime regDate;

    @CreatedDate
    @Column(name = "modDate")
    private LocalDateTime modDate;


}
