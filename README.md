# electoral-bonds-processing-svc
This is a service provides facility to process the electoral bonds data published by ECI.
Use sample files to process from uploaded-archive-files.
SQL's 

 SELECT SUM(denomination_amount) AS TotalDonationAmount
FROM electroral_bonds.fund_donor;
 SELECT SUM(denomination_amount) AS TotalDonationAmount
FROM electroral_bonds.fund_receiver where nameof_the_political_party='BHARATIYA JANATA PARTY';
select * FROM electroral_bonds.fund_donor;
select * FROM electroral_bonds.fund_receiver;
SELECT count(*) FROM electroral_bonds.fund_receiver;
SELECT count(*)  FROM electroral_bonds.fund_donor;
select count(distinct(bond_number) )FROM electroral_bonds.fund_donor;
select count(distinct(bond_number) )FROM electroral_bonds.fund_receiver;
SELECT distinct(SUBSTRING(bond_number, 1, 2)) AS PREFIX FROM electroral_bonds.fund_receiver;

select fr.nameof_the_political_party, fd.* FROM electroral_bonds.fund_receiver fr inner join electroral_bonds.fund_donor fd on fr.bond_number=fd.bond_number where fr.nameof_the_political_party='BHARATIYA JANATA PARTY';

select SUM(fr.denomination_amount) AS ReceivedAmt, fr.nameof_the_political_party
FROM electroral_bonds.fund_receiver fr inner join electroral_bonds.fund_donor fd on fr.bond_number=fd.bond_number where fr.nameof_the_political_party='BHARATIYA JANATA PARTY';

select * from electroral_bonds.fund_donor where bond_number in (select distinct(bond_number) from electroral_bonds.fund_receiver where nameof_the_political_party='ALL INDIA ANNA DRAVIDA MUNNETRA KAZHAGAM');
select distinct(bond_number) from electroral_bonds.fund_receiver where nameof_the_political_party='ALL INDIA ANNA DRAVIDA MUNNETRA KAZHAGAM';
select count(distinct(bond_number) )from electroral_bonds.fund_receiver where nameof_the_political_party='ALL INDIA ANNA DRAVIDA MUNNETRA KAZHAGAM';

select * from fund_donor where bond_number in ('OC3975');
