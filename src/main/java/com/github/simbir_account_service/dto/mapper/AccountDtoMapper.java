package com.github.simbir_account_service.dto.mapper;

import com.github.simbir_account_service.entity.account.Account;
import com.github.simbir_account_service.dto.AccountDto;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class AccountDtoMapper implements Function<Account, AccountDto> {
    @Override
    public AccountDto apply(Account account) {
        return new AccountDto(
                account.getId(),
                account.getUsername(),
                account.getLastName(),
                account.getFirstName(),
                account.getRole()
        );
    }
}
