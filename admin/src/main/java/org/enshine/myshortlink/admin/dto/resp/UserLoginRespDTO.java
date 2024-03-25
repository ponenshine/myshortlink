package org.enshine.myshortlink.admin.dto.resp;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginRespDTO {
    private String token;
}
