create table code
(
    code_id   CHAR(16) FOR BIT DATA not null,
    created   timestamp    not null,
    length    integer      not null,
    pool      varchar(255) not null,
    code_text varchar(255) not null,
    match_id  CHAR(16) FOR BIT DATA,
    user_id   CHAR(16) FOR BIT DATA,
    primary key (code_id)
);

create table codebreaker_match
(
    match_id          CHAR(16) FOR BIT DATA not null,
    code_length       integer      not null,
    codes_to_generate integer      not null,
    created           timestamp    not null,
    criterion         integer      not null,
    ending            timestamp    not null,
    pool              varchar(255) not null,
    originator_id     CHAR(16) FOR BIT DATA not null,
    primary key (match_id)
);

create table guess
(
    guess_id      CHAR(16) FOR BIT DATA not null,
    created       timestamp    not null,
    exact_matches integer      not null,
    near_matches  integer      not null,
    guess_text    varchar(255) not null,
    code_id       CHAR(16) FOR BIT DATA not null,
    user_id       CHAR(16) FOR BIT DATA not null,
    primary key (guess_id)
);

create table user_match_participation
(
    user_id  CHAR(16) FOR BIT DATA not null,
    match_id CHAR(16) FOR BIT DATA not null
);

create table user_profile
(
    user_id      CHAR(16) FOR BIT DATA not null,
    created      timestamp    not null,
    display_name varchar(255) not null,
    inactive     boolean      not null,
    oauth_key    varchar(255) not null,
    primary key (user_id)
);

create
index IDX97ckohcnvmrka14mi7ken9uo0 on codebreaker_match (code_length, pool);

create
index IDXtgbgv7bc1pg82w45lr3pxuaah on codebreaker_match (codes_to_generate);

create
index IDXp67xkagihad39fhiwfyt46b5t on codebreaker_match (ending);

alter table user_profile
    add constraint UK_j35xlx80xoi2sb176qdrtoy69 unique (display_name);

alter table user_profile
    add constraint UK_6f815wi5o4jq8p1q1w63o4mhd unique (oauth_key);

alter table code
    add constraint FKm69keqdjnj37kp5xs2pax5h6k foreign key (match_id) references codebreaker_match;

alter table code
    add constraint FKip6mj9uvygeluir0l8phl06h2 foreign key (user_id) references user_profile;

alter table codebreaker_match
    add constraint FKhugfih2t0j65tj8x0qi3ujfr foreign key (originator_id) references user_profile;

alter table guess
    add constraint FKwak3gf9mjwbqqrneoqv4jq78 foreign key (code_id) references code;

alter table guess
    add constraint FKs7oupt90tjhiw56as08pso6bu foreign key (user_id) references user_profile;

alter table user_match_participation
    add constraint FK27wf9hxr65ktnukc1aqb74b13 foreign key (match_id) references codebreaker_match;

alter table user_match_participation
    add constraint FKiwy0bph3h597792xvl9royhgp foreign key (user_id) references user_profile;

