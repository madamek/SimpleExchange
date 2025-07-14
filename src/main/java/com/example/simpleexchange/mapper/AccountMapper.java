package com.example.simpleexchange.mapper;

import com.example.simpleexchange.account.dto.*;
import com.example.simpleexchange.account.persistence.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "id", target = "accountId")
    AccountResponse toAccountResponse(Account account);
}
