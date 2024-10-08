package vn.eledevo.vksbe.dto.response.room;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.eledevo.vksbe.constant.RoomStatus;
import vn.eledevo.vksbe.dto.response.roomtype.RoomTypeResponseDTO;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RoomResponse {
    Integer id;
    String name;
    String roomNumber;
    String floor;
    RoomTypeResponseDTO roomType;
    String description;
    String price;

    @Enumerated(EnumType.STRING)
    RoomStatus status;
}
