create database javaproject;
use javaproject;


create table createaccount
(
accountno int auto_increment primary key,
password int,
name varchar(18),
balance  int);


create table depositmoney
(depositid int auto_increment primary key,
accountno int,
amount int,
deposittime timestamp default current_timestamp
);

create table withdrawmoney
(withdrawid int auto_increment primary key,
accountno int,
amount int,
withdrawtime timestamp default current_timestamp
);
create table transfermoney
(transferid int auto_increment primary key,
fromaccountno int ,
toaccountno int,
transfertime timestamp default current_timestamp,
amount int);




select * FROM createaccount;
select * from depositmoney;
select * from withdrawmoney;
select * from transfermoney;

