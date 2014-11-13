/* Populate the patient table. */
insert into patient values (123456789, 'John Smith', '123 Fake Road, Edmonton, AB', '01-Jan-1980', '1234567890');
insert into patient values (289555855, 'Susan Boyle', '1626 23 Ave. NW, Edmonton, AB', '19-Mar-1992', '7804877263');
insert into patient values (271172564, 'Nikias Hildiberht', '141 13 St. NW, Calgary, AB', '21-Feb-1991', '7806401627');
insert into patient values (505812752, 'Thom Alpha', '1000 Long St., Grand Prarie, AB', '30-Dec-1972', '4732928039');
insert into patient values (935687298, 'Bebe Michaels', '643 Dickensfield Rd., Fort McMurray, AB', '24-Mar-1961', '7807912009');
insert into patient values (467628601, 'Idris Christopher', '5411 34 St., Edmonton, AB', '22-Nov-1989', '7804562387');

/* Populate the doctor table. 
   Note: Susan Boyle is both a patient and a doctor. She is the first entry into the doctor table. */
insert into doctor values (12345, '123 Doctor Road, Edmonton, AB', '7805467283', '7804877263', 289555855);
insert into doctor values (11422, '1241 West Side St., Edmonton, AB', '7807467283', '7802637847', 935687298);
insert into doctor values (43529, '187 Silin Forest Rd., Fort McMurray, AB', '7807910298', '7807439182', 467628601);

/* Populate the medical_lab table. */
insert into medical_lab values ('Smart Lab Inc.', '142 Lab Street, Edmonton, AB', '7804939285');
insert into medical_lab values ('DynaLab', '1412 50 St. NW, Edmonton, AB', '7804529383');
insert into medical_lab values ('Super Healthy Needles', '412 77 Ave. NW, Calgary, AB', '7806153735');
insert into medical_lab values ('Blood Test Central', '3142 East-West Plaza, Grand Prarie, AB', '5312346374');

/* Populate the test_type table. */
insert into test_type values (312, 'CT scan', 'Cannot have metal in the body.', 'Patient will enter a large imaging device that will take X-rays of his/her body.');
insert into test_type values (524, 'X ray', 'Cannot have metal in the body.', 'Patient will enter a large imaging device and be still.');
insert into test_type values (872, 'Blood test', 'Blood test form must be completed.', 'Blood will be extracted from patient.');
insert into test_type values (209, 'Allergy test', 'Patient must be more than 2 years old.', 'Patient will be tested for allergies.');
insert into test_type values (746, 'bone marrow check', 'Patient must have written consent,', 'Bone marrow will be checked.');

/* Populate the can_conduct table. */
insert into can_conduct values ('Smart Lab Inc.', 312);
insert into can_conduct values ('Smart Lab Inc.', 524);
insert into can_conduct values ('Smart Lab Inc.', 872);
insert into can_conduct values ('Smart Lab Inc.', 209);
insert into can_conduct values ('Smart Lab Inc.', 746);
insert into can_conduct values ('DynaLab', 312);
insert into can_conduct values ('DynaLab', 524);
insert into can_conduct values ('Super Healthy Needles', 872);
insert into can_conduct values ('Super Healthy Needles', 209);
insert into can_conduct values ('Blood Test Central', 872);

/* Populate the not_allowed table. */
insert into not_allowed values (289555855, 312);
insert into not_allowed values (289555855, 524);
insert into not_allowed values (271172564, 872);
insert into not_allowed values (271172564, 746);
insert into not_allowed values (505812752, 209);
insert into not_allowed values (505812752, 524);
insert into not_allowed values (935687298, 746);

/* Populate the test_record table. */
insert into test_record values (0001, 872, 123456789, 12345, 'Smart Lab Inc.', 'No serious complications.', '19-Apr-2009', '26-Apr-2009');
insert into test_record values (0002, 872, 123456789, 12345, 'Smart Lab Inc.', 'No serious complications.', '19-May-2009', '23-Jun-2009');
insert into test_record values (0003, 872, 123456789, 12345, 'Smart Lab Inc.', 'No serious complications.', '30-Aug-2010', '11-Oct-2010');
insert into test_record values (0004, 872, 123456789, 12345, 'Smart Lab Inc.', 'Serious complications were found.', '04-Jan-2012', '07-Jan-2012');
insert into test_record values (0005, 872, 123456789, 43529, 'Blood Test Central', 'No serious complications.', '08-Jan-2012', '31-Jan-2012');
insert into test_record values (0006, 524, 271172564, 11422, 'DynaLab', 'Routine X-ray checkup.', '14-Dec-2011', '15-Dec-2011');
insert into test_record values (0007, 524, 271172564, 11422, 'DynaLab', 'Routine X-ray checkup.', '25-Sep-2013', '31-Oct-2013');
insert into test_record values (0008, 312, 271172564, 43529, 'DynaLab', 'Routine CT scan.', '25-Sep-2014', '26-Sep-2014');
insert into test_record values (0009, 209, 289555855, 43529, 'Super Healthy Needles', 'Routine allergy test.', '13-Nov-2012', '02-Feb-2013');
insert into test_record values (0010, 312, 505812752, 43529, 'Smart Lab Inc.', 'Routine CT scan.', '17-Jan-2011', '21-Jan-2011');
insert into test_record values (0011, 312, 467628601, 12345, 'DynaLab', 'Routine CT scan.', '10-Mar-2013', '12-Mar-2013');
insert into test_record values (0012, 312, 467628601, 12345, 'DynaLab', 'Routine CT scan.', '19-Mar-2013', '21-Mar-2013');
insert into test_record values (0013, 312, 467628601, 12345, 'DynaLab', 'Routine CT scan.', '27-Mar-2013', '28-Mar-2013');



/* commit; */