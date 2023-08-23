package com.example.demo.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Builder
@AllArgsConstructor
@ToString
@Data
public class ErrorDto  implements Serializable {
    public final String errorPath;
    public final String errorMessage;
    public final int errorCode;
    public final Date date;
}