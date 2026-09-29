package cz.inqool.eas.eil.user;

import cz.inqool.eas.common.dated.DatedApi;
import cz.inqool.eas.common.exception.dto.RestException;
import cz.inqool.eas.eil.user.dto.ChangePasswordDto;
import cz.inqool.eas.eil.user.dto.ChangeRoleDto;
import cz.inqool.eas.eil.user.dto.PasswordResetDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Tag(name = "Users API", description = "Api for working with users")
@RestController
@RequestMapping("/user")
public class UserApi extends DatedApi<
        User,
        UserDetail,
        UserList,
        UserCreate,
        UserUpdate,
        UserService
        > {

    @Override
    @Operation(summary = "Creates new user")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Wrong input was specified", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping
    public UserDetail create(@Valid @RequestBody UserCreate view) {
        return service.create(view);
    }

    @Operation(summary = "Confirm user registration")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Wrong input was specified", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping("confirm-reg/{tokenId}")
    public boolean confirmRegistration(@NotNull @PathVariable("tokenId") String tokenId) {
        return service.confirmRegistration(tokenId);
    }

    @Operation(summary = "Change user password")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Wrong input was specified", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PutMapping("change-pw")
    public UserDetail changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        return service.changePassword(dto);
    }

    @Operation(summary = "Change user role")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PutMapping("change-role")
    public boolean changeRole(@Valid @RequestBody ChangeRoleDto dto) {
        return service.changeRole(dto);
    }

    @Operation(summary = "Request password reset")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/request-password-reset/{email}")
    public void requestPasswordResetEmail(@PathVariable("email") String email) {
        service.requestPasswordResetEmail(email);
    }

    @Operation(summary = "Reset password")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "403", description = "Forbidden action", content = @Content(schema = @Schema(implementation = RestException.class)))
    @ApiResponse(responseCode = "404", description = "Object not found", content = @Content(schema = @Schema(implementation = RestException.class)))
    @PostMapping(value = "/reset-password")
    public void resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        service.resetPassword(passwordResetDto);
    }
}
