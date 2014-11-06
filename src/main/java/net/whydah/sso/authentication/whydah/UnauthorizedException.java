package net.whydah.sso.authentication.whydah;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason="Not authorized")
class UnauthorizedException extends RuntimeException {
}
