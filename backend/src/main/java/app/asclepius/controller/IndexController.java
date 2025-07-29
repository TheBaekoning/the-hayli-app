package app.asclepius.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class IndexController {
    @GetMapping("/csrf")
    @Operation(summary = "Anonymous accessible request to retrieve csrf token")
    public CsrfToken csrf(@Parameter(hidden = true) CsrfToken csrfToken) {
        return csrfToken;
    }
}
