package com.example.simpleexchange.account.persistence;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
}
