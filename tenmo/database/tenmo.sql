BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transfer, request;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id, seq_request_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance decimal(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);


CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer (
	transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
	from_account int NOT NULL,
	to_account int NOT NULL,
	transfer_amount decimal(13, 2) NOT NULL,
	time_stamp timestamp DEFAULT current_timestamp,
	status varchar(10) NOT NULL,
	CONSTRAINT PK_transfer PRIMARY KEY (transfer_id),
	CONSTRAINT FK_transfer_account_from FOREIGN KEY (from_account) REFERENCES account (account_id),
	CONSTRAINT FK_transfer_account_to FOREIGN KEY (to_account) REFERENCES account (account_id)
);

CREATE SEQUENCE seq_request_id
  INCREMENT BY 1
  START WITH 4001
  NO MAXVALUE;

CREATE TABLE request (
	request_id int NOT NULL DEFAULT nextval('seq_request_id'),
	requestor_account int NOT NULL,
	requestee_account int NOT NULL,
	requested_amount decimal(13, 2) NOT NULL,
	time_stamp timestamp,
	status varchar(10) NOT NULL,
	CONSTRAINT PK_request PRIMARY KEY (request_id),
	CONSTRAINT FK_request_account_requestor FOREIGN KEY (requestor_account) REFERENCES account (account_id),
	CONSTRAINT FK_request_account_requestee FOREIGN KEY (requestee_account) REFERENCES account (account_id)
);



COMMIT;