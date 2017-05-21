package com.izettle.assignment;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.ttl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Select;
import com.izettle.assignment.AppConstants;
import com.izettle.assignment.entity.LoginAudit;
import com.izettle.assignment.entity.LoginAudit;
import com.izettle.assignment.entity.User;
import com.izettle.assignment.utils.ExceptionCreator;

public class JunitDao {

    private final Session session;
    private final static String SMS_CODE_TABLE = "sms_2f_codes";
    protected static PreparedStatement insertApplication;
    protected static PreparedStatement insertUserGroup;
    protected static PreparedStatement insertRole;
    protected static PreparedStatement insertUser;
    protected static PreparedStatement insertUserGroupAssignment;
    protected static PreparedStatement insertUserKeys;

    private static PreparedStatement getAllUserKeysRowKeys;
    private static PreparedStatement deleteUserKeysRow;
    private static PreparedStatement getDrwpKeysRowKeys;
    private static PreparedStatement deleteDrwpKeysRow;
    private static PreparedStatement getAllPermissions;
    private static PreparedStatement deletePermissions;
    private static PreparedStatement getAllOperations;
    private static PreparedStatement deleteOperations;
    private static PreparedStatement getAllRoles;
    private static PreparedStatement deleteRoles;
    private static PreparedStatement deleteOperationPermissionAssignment;
    private static PreparedStatement deletePermissionRoleAssignment;
    private static PreparedStatement getUsers;
    private static PreparedStatement deleteUsers;
    private static PreparedStatement getAllApplications;
    private static PreparedStatement deleteApplications;
    private static PreparedStatement getAllUserGroups;
    private static PreparedStatement deleteUserGroups;
    private static PreparedStatement getAllUserGroupAssignments;
    private static PreparedStatement deleteUserGroupAssignments;
    private static PreparedStatement getAllRefreshTokens;
    private static PreparedStatement deleteRefreshTokens;
    private static PreparedStatement getAllUserRefreshTokens;
    private static PreparedStatement deleteUserRefreshTokens;
    private static PreparedStatement getAllAuthzCodes;
    private static PreparedStatement deleteUserAuthCodes;
    private static PreparedStatement deleteAuthCodes;
    private static PreparedStatement getAllAuditLogins;
    private static PreparedStatement deleteAllAuditLogins;
    private static PreparedStatement getAllActiveRefreshTokens;
    private static PreparedStatement deleteActiveRefreshTokens;
    private static PreparedStatement getAllCidrAddresses;
    private static PreparedStatement deleteCidrAddresses;
    private static PreparedStatement getSmsCodes;
    private static PreparedStatement deleteSmsCodes;
    private static PreparedStatement getSmsAttempts;
    private static PreparedStatement deleteSmsAttempts;
    private static PreparedStatement get2LBearerUserToken;
    private static PreparedStatement delete2LBearerUserToken;
    private final static String AUTHORIZATION_CODE_TABLE = "authorization_codes";

    public JunitDao(final Session session) {
        this.session = session;
        prepareStatements();
    }

    private void prepareStatements() {
        insertApplication = session.prepare("INSERT INTO security_service.applications" + "(client_application_id, "
                + "uri," + "client_application_name," + "client_secret," + "bearer_expiration,"
                + "refresh_token_expiration," + "updated_by," + "updated_timestamp, " + "create_timestamp,"
                + "created_by, max_allowed_refresh , is_active) values (?,?,?,?,?,?,?,?,?,?,?,?)");
        insertUserGroup = session.prepare(
                "INSERT INTO security_service.usergroups (master_mid,group,roles,mids, pos_ids,submerchants,updated_by,updated_timestamp,create_timestamp,created_by, is_active) values(?,?,?,?,?,?,?,?,?,?,?)");

        insertRole = session.prepare(
                "INSERT INTO security_service.roles (role_name,permission,operations,updated_by, updated_timestamp,create_timestamp,created_by, is_active) values(?,?,?,?,?,?,?,?)");

        insertUser = session.prepare(
                "INSERT INTO security_service.users (user_name,password,salt,is_active_user, master_mid,is_two_legged_user,two_legged_bearer_validity_max_seconds,two_legged_bearer_attemps,is_encryption_enabled,updated_by,updated_timestamp,create_timestamp,created_by,first_name,last_name,mobile_number,failed_login_attempts,last_login_attempt,password_expiration_timestamp,is_locked_out,is_two_factor_authentication_enabled,two_factor_authentication_type, user_directory, is_email_verified) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        insertUserGroupAssignment = session.prepare(
                "INSERT INTO security_service.user_group_assignments (master_mid, user_name,group,updated_by,updated_timestamp,create_timestamp,created_by, is_active) values(?,?,?,?,?,?,?,?)");
        insertUserKeys = session.prepare(
                "INSERT INTO security_service.sec_user_keys (user_id,public_key,create_timestamp,updated_timestamp,isactive) values(?,?,?,?,?)");
        getAllUserKeysRowKeys = session.prepare("select user_id from security_service.sec_user_keys");
        deleteUserKeysRow = session.prepare("DELETE FROM security_service.sec_user_keys where user_id=?");

        getDrwpKeysRowKeys = session.prepare("select dr_company_id FROM security_service.sec_drwp_keys");
        deleteDrwpKeysRow = session.prepare("DELETE FROM security_service.sec_drwp_keys where dr_company_id=?");

        getAllOperations = session.prepare("select resource_owner, operation_name from security_service.operations");
        deleteOperations = session.prepare("DELETE FROM security_service.operations where resource_owner=?");

        getAllPermissions = session.prepare("select permission from security_service.permissions");
        deletePermissions = session.prepare("DELETE FROM security_service.permissions where permission=?");

        getAllRoles = session.prepare("select role_name from security_service.roles");
        deleteRoles = session.prepare("DELETE FROM security_service.roles where role_name=?");

        deleteOperationPermissionAssignment = session
                .prepare("DELETE FROM security_service.operation_permission_assignments where operation_name=?");
        deletePermissionRoleAssignment = session
                .prepare("DELETE FROM security_service.permission_role_assignments where permission=?");

        getUsers = session.prepare("select user_name from security_service.users");
        deleteUsers = session.prepare("DELETE FROM security_service.users where user_name=?");

        getAllAuditLogins = session.prepare("select master_mid, user_name from security_service.user_login_audits");
        deleteAllAuditLogins = session
                .prepare("DELETE FROM security_service.user_login_audits where master_mid = ? AND user_name = ?");

        getAllApplications = session.prepare("select client_application_id from security_service.applications");
        deleteApplications = session.prepare("DELETE FROM security_service.applications where client_application_id=?");

        getAllUserGroups = session.prepare("select master_mid from security_service.usergroups");
        deleteUserGroups = session.prepare("DELETE FROM security_service.usergroups where master_mid=?");

        getAllUserGroupAssignments = session.prepare("select master_mid from security_service.user_group_assignments");
        deleteUserGroupAssignments = session
                .prepare("DELETE FROM security_service.user_group_assignments where master_mid=?");

        getAllRefreshTokens = session.prepare("select refresh_token from security_service.refresh_tokens");
        deleteRefreshTokens = session.prepare("DELETE FROM security_service.refresh_tokens where refresh_token=?");

        getAllUserRefreshTokens = session.prepare("select user_name from security_service.users_refresh_tokens");
        deleteUserRefreshTokens = session
                .prepare("DELETE FROM security_service.users_refresh_tokens where user_name=?");

        getAllAuthzCodes = session.prepare("select auth_code,user_name from security_service.authorization_codes");
        deleteUserAuthCodes = session
                .prepare("DELETE FROM security_service.user_authorization_codes where user_name=?");
        deleteAuthCodes = session.prepare("DELETE FROM security_service.authorization_codes where auth_code=?");

        getAllActiveRefreshTokens = session
                .prepare("select user_name from security_service.user_active_refresh_tokens");
        deleteActiveRefreshTokens = session
                .prepare("DELETE FROM security_service.user_active_refresh_tokens where user_name=?");
        getAllCidrAddresses = session.prepare("select cidr_address from security_service.cidr_addresses");
        deleteCidrAddresses = session.prepare("DELETE FROM security_service.cidr_addresses where cidr_address=?");
        getSmsCodes = session.prepare("select user_name, client_application_id from security_service.sms_2f_codes");
        deleteSmsCodes = session.prepare("DELETE FROM security_service.sms_2f_codes where user_name=? and client_application_id=?");
        getSmsAttempts = session.prepare("select authz_code from security_service.sms_failed_attempts");
        deleteSmsAttempts = session.prepare("DELETE FROM security_service.sms_failed_attempts where authz_code=?");
        get2LBearerUserToken = session.prepare("select user_name, time_slot_min from security_service.two_legged_user_issued_bearer_tokens_ts");
        delete2LBearerUserToken = session.prepare("DELETE FROM security_service.two_legged_user_issued_bearer_tokens_ts where user_name=? and time_slot_min=?");
    }

    public void wipeData() {

        wipeUsers();
        wipeApplications();

        wipeAuthorizationCodes();
        wipeLoginAuditData();

        wipe2LUserBearerTokens();
    }

//    public List<LoginAudit> getLoginAuditByMM(final String masterMid, final String userName) {
//        final Statement statement = getSelectStatement("user_login_audits").where(eq("master_mid", masterMid))
//                .and(eq("user_name", userName));
//        final ResultSet res = session.execute(statement);
//        final List<LoginAudit> loginAudits = new ArrayList<>();
//        while (!res.isExhausted()) {
//            Row row = res.one();
//            loginAudits.add(createLoginAuditFromDbResponse(row));
//        }
//        return loginAudits;
//    }

//    public List<LoginAudit> getLoginAudits() {
//        final Statement statement = getSelectStatement("user_login_audits");
//        final ResultSet res = session.execute(statement);
//        final List<LoginAudit> loginAudits = new ArrayList<>();
//        while (!res.isExhausted()) {
//            Row row = res.one();
//            loginAudits.add(createLoginAuditFromDbResponse(row));
//        }
//        return loginAudits;
//    }

    private void wipeLoginAuditData() {
        BoundStatement bound = getAllAuditLogins.bind();
        ResultSet res = session.execute(bound);
        while (!res.isExhausted()) {
            Row r = res.one();
            BoundStatement del = deleteAllAuditLogins.bind(r.getString("master_mid"), r.getString("user_name"));
            session.execute(del);
        }
    }

    public void wipeUsers() {
        BoundStatement bound = getUsers.bind();
        ResultSet res = session.execute(bound);
        while (!res.isExhausted()) {
            Row r = res.one();
            BoundStatement del = deleteUsers.bind(r.getString("user_name"));
            session.execute(del);
        }
    }

    private void wipeApplications() {
        BoundStatement bound = getAllApplications.bind();
        ResultSet res = session.execute(bound);
        while (!res.isExhausted()) {
            Row r = res.one();
            BoundStatement del = deleteApplications.bind(r.getString("client_application_id"));
            session.execute(del);
        }
    }

    private void wipeAuthorizationCodes() {
        BoundStatement bound = getAllAuthzCodes.bind();
        ResultSet res = session.execute(bound);
        while (!res.isExhausted()) {
            Row r = res.one();
            BoundStatement del = deleteAuthCodes.bind(r.getString("auth_code"));
            session.execute(del);
            BoundStatement del2 = deleteUserAuthCodes.bind(r.getString("user_name"));
            session.execute(del2);
        }
    }

    private void wipe2LUserBearerTokens() {
        BoundStatement bound = get2LBearerUserToken.bind();
        ResultSet res = session.execute(bound);
        while (!res.isExhausted()) {
            Row r = res.one();
            session.execute(delete2LBearerUserToken.bind(r.getString("user_name"), r.getString("time_slot_min")));
        }
    }

  

//    public void saveUser(final User user) {
//        final BoundStatement boundCreate = insertUser.bind(user.getUserName(), user.getPassword(), user.getSalt(),
//                user.getIsActiveUser(), user.getMasterMid(), user.getIsTwoLeggedUser(),
//                user.getTwoLeggedBearerTokenValiditySeconds(), user.getTwoLeggedBearerAttemps(),
//                user.getIsEncryptionEnabled(), user.getUpdatedBy(), user.getUpdatedTimestamp(),
//                user.getCreatedTimestamp(), user.getCreatedBy(), user.getFirstName(), user.getLastName(),
//                user.getMobileNumber(), user.getFailedLoginAttempts(), user.getLastLoginAttempt(),
//                user.getPasswordExpirationTimestamp(), user.getIsLockedOut(),
//                user.getIsTwoFactorAuthenticationEnabled(), user.getTwoFactorAuthenticationType(),
//                user.getUserDirectory(), user.getIsEmailVerified());
//        session.execute(boundCreate);
//    }



    protected Select getSelectStatement(final String tableName) {
        return select().all().from(AppConstants.IZETTLE_SVC_KEYSPACE, tableName);
    }

    protected Insert getInsertStatement(final String tableName) {
        return insertInto(AppConstants.IZETTLE_SVC_KEYSPACE, tableName);
    }

//    private LoginAudit createLoginAuditFromDbResponse(final Row row) {
//        LoginAudit loginAudit = new LoginAudit();
//        loginAudit.setIpAddress(row.getString("ip_address"));
//        //loginAudit.setLoginTs(new Timestamp(row.getDate("login_ts").getTime()));
//        loginAudit.setMasterMid(row.getString("master_mid"));
//        loginAudit.setMessage(row.getString("message"));
//        loginAudit.setReferer(row.getString("referer"));
//        loginAudit.setStatus(row.getString("status"));
//        loginAudit.setUserName(row.getString("user_name"));
//        loginAudit.setClientBrowserInfo(row.getString("client_browser_info"));
//        loginAudit.setClientOperatingSystem(row.getString("client_operating_system"));
//        loginAudit.setClientDeviceType(row.getString("client_device_type"));
//        return loginAudit;
//    }



    protected Timestamp getNowTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    private Timestamp getTimestamp(final Date date) {
        return date != null ? new Timestamp(date.getTime()) : null;
    }



}
