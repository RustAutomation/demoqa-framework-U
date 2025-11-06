package framework.api.DTO;

import lombok.Builder;
import lombok.Value;

/**
 * DTO для создания или обновления пользователя.
 */
@Value
@Builder
public class UserRequest {
    String name;
    String job;
}