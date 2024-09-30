package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseUiModel


@Dao
interface MoneyJournalDao {

    @Insert
    suspend fun insetMoneyJournalEntry(moneyJournalEntity: MoneyJournalEntity)

    @Insert
    suspend fun insertMoneyJournalEntry(moneyJournalEntity: List<MoneyJournalEntity>)

    @Query("select * from money_journal_table where userId=:userId and subjectId=:subjectId and transactionId=:transactionId and status=1")
    suspend fun getMoneyJournalForTransaction(
        userId: String,
        transactionId: String,
        subjectId: Int
    ): MoneyJournalEntity?

    @Query("Select count(*) from money_journal_table where userId=:userId and transactionId=:transactionId and status=1")
    suspend fun isTransactionAlreadyExist(userId: String, transactionId: String): Int

    @Query("Select * from money_journal_table where userId=:userId and transactionId=:transactionId and status=1")
    suspend fun getMoneyJournalTransaction(
        userId: String,
        transactionId: String
    ): MoneyJournalEntity

    @Query("update money_journal_table set transactionAmount=:amount, transactionDate=:date, transactionDetails=:particulars, modifiedDate=:modifiedDate where userId=:userId and transactionId=:transactionId ")
    suspend fun updateMoneyJournal(
        transactionId: String,
        userId: String,
        date: Long,
        amount: Double,
        particulars: String,
        modifiedDate: Long = System.currentTimeMillis()
    )


    @Query("update money_journal_table set status=2 where transactionId=:transactionId and subjectId=:subjectId and userId=:userId")
    suspend fun softDeleteTransaction(
        transactionId: String,
        subjectId: Int,
        userId: String
    )

    @Query("update money_journal_table set status=0 where transactionId=:transactionId and userId=:userId and transactionFlow=:transactionFlow")
    suspend fun softDeleteTransaction(
        transactionId: String,
        transactionFlow: String,
        userId: String
    )

    @Query("Delete from money_journal_table where userId=:userId")
    suspend fun deleteMoneyJournal(userId: String)


    @Query("select subjectId as subjectId, sum(transactionAmount) as totalIncome from money_journal_table where userId = :userId and subjectId = :subjectId and transactionFlow = :transactionFlow and referenceType = :referenceType and status=1 group by subjectId")
    suspend fun getTotalIncomeExpenseForSubject(
        transactionFlow: String,
        userId: String,
        subjectId: Int,
        referenceType: String
    ): IncomeExpenseUiModel?

    @Query("select subjectId as subjectId, sum(transactionAmount) as totalIncome from money_journal_table where userId = :userId and subjectId = :subjectId and transactionFlow = :transactionFlow and referenceType = :referenceType and referenceId = :referenceId and status=1 group by subjectId")
    suspend fun getTotalIncomeExpenseForSubject(
        transactionFlow: String,
        userId: String,
        subjectId: Int,
        referenceType: String,
        referenceId: Int
    ): IncomeExpenseUiModel?

    @Query("select subjectId as subjectId, sum(transactionAmount) as totalIncome from money_journal_table where userId = :userId and subjectId = :subjectId and transactionFlow = :transactionFlow and referenceType = :referenceType and transactionDate BETWEEN :durationStart and :durationEnd and status=1 group by subjectId")

    suspend fun getTotalIncomeExpenseForSubjectForDuration(
        transactionFlow: String,
        userId: String,
        subjectId: Int,
        referenceType: String,
        durationStart: Long,
        durationEnd: Long
    ): IncomeExpenseUiModel?

    @Query("select count(*) from money_journal_table where userId=:userId")
    suspend fun isMoneyJournalExist(userId: String): Int
}