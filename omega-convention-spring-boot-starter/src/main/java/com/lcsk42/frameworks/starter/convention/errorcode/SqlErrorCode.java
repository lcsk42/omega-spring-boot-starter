package com.lcsk42.frameworks.starter.convention.errorcode;

import com.lcsk42.frameworks.starter.convention.enums.BusinessDomainEnum;
import com.lcsk42.frameworks.starter.convention.enums.ErrorSourceEnum;
import com.lcsk42.frameworks.starter.convention.model.ErrorNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SqlErrorCode implements ErrorCode {


    RECORD_NOT_FOUND(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(1),
            "Record Not Found"),
    RECORD_ALREADY_EXISTS(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(2),
            "Record Already Exists"),
    DUPLICATE_KEY(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(3),
            "Duplicate Key Violation"),
    DATA_INTEGRITY_VIOLATION(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(4),
            "Data Integrity Violation"),
    FOREIGN_KEY_VIOLATION(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(5),
            "Foreign Key Violation"),
    UNIQUE_CONSTRAINT_VIOLATION(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(6),
            "Unique Constraint Violation"),
    NOT_NULL_CONSTRAINT_VIOLATION(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(7),
            "Not Null Constraint Violation"),
    CHECK_CONSTRAINT_VIOLATION(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(8),
            "Check Constraint Violation"),
    COLUMN_OUT_OF_RANGE(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(9),
            "Column Out Of Range"),
    DATA_TYPE_MISMATCH(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(10),
            "Data Type Mismatch"),
    INVALID_SQL_PARAMETER(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(11),
            "Invalid Sql Parameter"),
    INVALID_PAGINATION_PARAMETER(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(12),
            "Invalid Pagination Parameter"),
    OPTIMISTIC_LOCK_CONFLICT(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(13),
            "Optimistic Lock Conflict"),
    PESSIMISTIC_LOCK_CONFLICT(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(14),
            "Pessimistic Lock Conflict"),
    RECORD_VERSION_MISMATCH(
            ErrorSourceEnum.CLIENT,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(15),
            "Record Version Mismatch"),

    SQL_EXECUTION_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(1),
            "Sql Execution Failed"),
    SQL_SYNTAX_ERROR(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(2),
            "Sql Syntax Error"),
    SQL_QUERY_TIMEOUT(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(3),
            "Sql Query Timeout"),
    SQL_UPDATE_TIMEOUT(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(4),
            "Sql Update Timeout"),
    SQL_DEADLOCK(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(5),
            "Sql Deadlock"),
    SQL_LOCK_TIMEOUT(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(6),
            "Sql Lock Timeout"),
    TRANSACTION_ROLLBACK(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(7),
            "Transaction Rollback"),
    TRANSACTION_COMMIT_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(8),
            "Transaction Commit Failed"),
    TRANSACTION_BEGIN_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(9),
            "Transaction Begin Failed"),
    TRANSACTION_NESTED_NOT_SUPPORTED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(10),
            "Transaction Nested Not Supported"),
    DATABASE_CONNECTION_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(11),
            "Database Connection Failed"),
    DATABASE_CONNECTION_TIMEOUT(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(12),
            "Database Connection Timeout"),
    DATABASE_CONNECTION_POOL_EXHAUSTED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(13),
            "Database Connection Pool Exhausted"),
    DATABASE_UNAVAILABLE(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(14),
            "Database Unavailable"),
    DATABASE_DISK_FULL(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(15),
            "Database Disk Full"),
    DATABASE_TABLE_NOT_FOUND(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(16),
            "Database Table Not Found"),
    DATABASE_COLUMN_NOT_FOUND(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(17),
            "Database Column Not Found"),
    DATABASE_INDEX_MISSING(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(18),
            "Database Index Missing"),
    DATABASE_PERMISSION_DENIED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(19),
            "Database Permission Denied"),
    DATABASE_MIGRATION_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(20),
            "Database Migration Failed"),
    DATABASE_MAPPING_FAILED(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(21),
            "Database Mapping Failed"),
    DATABASE_UNKNOWN_EXCEPTION(
            ErrorSourceEnum.SERVICE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(22),
            "Database Unknown Exception"),

    REMOTE_DATABASE_UNREACHABLE(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(1),
            "Remote Database Unreachable"),
    REMOTE_DATABASE_TIMEOUT(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(2),
            "Remote Database Timeout"),
    REMOTE_REPLICATION_LAG(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(3),
            "Remote Replication Lag"),
    REMOTE_REPLICATION_FAILED(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(4),
            "Remote Replication Failed"),
    REMOTE_READ_ONLY(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(5),
            "Remote Read Only"),
    REMOTE_FAILOVER_IN_PROGRESS(
            ErrorSourceEnum.REMOTE,
            BusinessDomainEnum.SQL,
            ErrorNumber.of(6),
            "Remote Failover In Progress"),

    ;

    /**
     * 错误来源（客户端/服务端/远程）。
     */
    private final ErrorSourceEnum errorSourceEnum;

    /**
     * 错误发生的业务域。
     */
    private final BusinessDomainEnum businessDomainEnum;

    /**
     * 业务域内的唯一错误编号。
     */
    private final ErrorNumber errorNumber;

    /**
     * 人类可读的错误消息。
     * <p>
     * 应提供足够的排错上下文信息，同时确保可以安全暴露给客户端。
     * </p>
     */
    private final String message;
}
