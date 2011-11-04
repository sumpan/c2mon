create table TIM_VIDEO_SYSTEMS (TVS_VIDEO_SYSTEM_ID     INTEGER NOT NULL PRIMARY KEY,TVS_VIDEO_SYSTEM_NAME   VARCHAR(255) NOT NULL )

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

create table TIM_VIDEOS(TIV_ID                        INTEGER NOT NULL ,TIV_VIDEO_SYSTEM_ID           INTEGER NOT NULL ,TIV_KEYSTAKENDATA_TAGID       INTEGER NULL ,TIV_REGISTRATIONDATA_TAGID    INTEGER NOT NULL ,TIV_ACTIVATIONDATA_TAGID      INTEGER NOT NULL ,TIV_HOST                      VARCHAR(255) NOT NULL ,TIV_CAMERA                    INTEGER NOT NULL ,TIV_LOGIN                     VARCHAR(255) NULL ,TIV_PASSWORD                  VARCHAR(255) NULL ,TIV_DESCRIPTION               VARCHAR(255) NULL )

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

create table TIM_VIDEO_RESTRICTIONS (TVR_VIDEO_RESTRICTION_ID    INTEGER NOT NULL ,TVR_VIDEO_SYSTEM_ID         INTEGER    NOT NULL ,TVR_ROLE_ID                 INTEGER    NOT NULL )

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

create table SEC_ROLE (ROLEID INTEGER NOT NULL,DOMAIN     VARCHAR(255)         NOT NULL  ,ROLEDESC                           VARCHAR(255),ROLENAME    VARCHAR(255)     NOT NULL             )

