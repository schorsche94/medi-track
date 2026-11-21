package com.schorsche94.medi_track.domain.mongodb.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("schedule_prepared")
public class SchedulePrepared {

    @Id
    private String id;

    private LocalDate date;

    private String dateLocal;

    public SchedulePrepared(LocalDate date) {
        this.date = date;
        this.dateLocal = date.toString();
    }
}
