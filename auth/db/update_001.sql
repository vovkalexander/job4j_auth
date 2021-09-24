create table person (
                        id serial primary key not null,
                        login varchar(2000),
                        password varchar(2000)
);

alter table person
    add column employee_id int not null references employee(id);


create table employee (
                          id serial primary key not null,
                          name character varying(2000),
                          surname character varying(2000),
                          tin integer,
                          created date DEFAULT now()
);


insert into person (login, password, employee_id) values ('parsentev', '123', 1);
insert into person (login, password, employee_id) values ('ban', '123', 1);
insert into person (login, password, employee_id) values ('ivan', '123', 1);

insert into person (login, password, employee_id) values ('buba', '321', 2);
insert into person (login, password, employee_id) values ('bones', '321', 2);
insert into person (login, password, employee_id) values ('ali', '787', 3);

insert into employee (name, surname, tin) values ('akhmed', 'akhmedkin', 1234567);
insert into employee (name, surname, tin) values ('pupa', 'pupkin', 78910);
insert into employee (name, surname, tin) values ('ashot', 'ashotkin', 101112);


