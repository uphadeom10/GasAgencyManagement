package com.agency.management.masters.repository;

import com.agency.management.masters.dto.response.BankAccountByIdResponseDto;
import com.agency.management.masters.dto.response.BankAccountResponseList;
import com.agency.management.masters.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountsRepository extends JpaRepository<BankAccount, Long> {

    @Query(value = "select * from fn_mt_get_account_by_id(?1)", nativeQuery = true)
    BankAccountByIdResponseDto getAccountById(Long id);

    @Query(value = "select * from fn_mt_get_bank_account_list(?1,?2,?3,?4)",nativeQuery = true)
    List<BankAccountResponseList> getBankAccountList(Long id,String searchString,Integer page,Integer size);

    @Query(value = "select * from fn_mt_get_bank_account_list_count(?1,?2)",nativeQuery = true)
    Long getBankAccountListCount(Long id,String searchString);

}
