
UPDATE [ED_SM2].[dbo].[PROD_KEYWORD_MEMBER] SET value = REPLACE(value,'InStock:1','VWInv:[1 TO *] OR MemphisInv:[1 TO *]') 
WHERE value like '%InStock:1%' AND PARENT_MEMBER_ID != 'opstrack';
UPDATE [ED_SM2].[dbo].[PROD_KEYWORD_MEMBER] SET value = REPLACE(value,'InStock:0','VWInv:[* TO 0] OR MemphisInv:[* TO 0]') 
WHERE value like '%InStock:0%' AND PARENT_MEMBER_ID != 'opstrack';
UPDATE [ED_SM].[dbo].[PROD_KEYWORD_MEMBER] SET value = REPLACE(value,'InStock:1','VWInv:[1 TO *] OR MemphisInv:[1 TO *]') 
WHERE value like '%InStock:1%' AND PARENT_MEMBER_ID != 'opstrack';
UPDATE [ED_SM].[dbo].[PROD_KEYWORD_MEMBER] SET value = REPLACE(value,'InStock:0','VWInv:[* TO 0] OR MemphisInv:[* TO 0]') 
WHERE value like '%InStock:0%' AND PARENT_MEMBER_ID != 'opstrack';
UPDATE [ED_SM].[dbo].[relevancy_fields] SET field_value = REPLACE(field_value,'map(InStock,1,10,8)^50','map(VWInv,1,9999,8)^50') 
WHERE FIELD_NAME='bf' and FIELD_VALUE like '%map(InStock%' ;
UPDATE [ED_SM2].[dbo].[relevancy_fields] SET field_value = REPLACE(field_value,'map(InStock,1,10,8)^50','map(VWInv,1,9999,8)^50') 
WHERE FIELD_NAME='bf' and FIELD_VALUE like '%map(InStock%'
UPDATE [ED_SM].[dbo].[relevancy_fields] SET field_value = REPLACE(field_value,'InStock','sum(MemphisInv,VWInv)') 
WHERE FIELD_NAME='bf' and FIELD_VALUE like '%InStock%';
UPDATE [ED_SM2].[dbo].[relevancy_fields] SET field_value = REPLACE(field_value,'InStock','sum(MemphisInv,VWInv)') 
WHERE FIELD_NAME='bf' and FIELD_VALUE like '%InStock%';
UPDATE [ED_SM].[dbo].[REDIRECT_RULE_CONDITION] SET condition = REPLACE(condition,'InStock:1','(VWInv:[1 TO *] OR MemphisInv:[1 TO *])') 
WHERE condition like '%InStock:1%';
UPDATE [ED_SM2].[dbo].[REDIRECT_RULE_CONDITION] SET condition = REPLACE(condition,'InStock:1','(VWInv:[1 TO *] OR MemphisInv:[1 TO *])') 
WHERE condition like '%InStock:1%';
--pcm 0064dYW0V0Q1MYGOZtA8
--mm 0064dYW0V0PzMj1TXaWI
--select * from [ED_SM].[dbo].[relevancy_fields]   WHERE RELEVANCY_ID='0064dYW0V0PzMj1TXaWI';
update [ED_SM].[dbo].[RELEVANCY_FIELDS] set field_value='GenericUser_Keywords^0 MacMall_Description^0 UPC^2 MacMall_ManufacturerIndex^8 MacMall_FacetTemplateNameIndex^8 MfrPN^6 MfrPN2^6 MacMall_Name^2 RelevantIDKey^4 UPC2^0 DPNo^0 EDP^0 MacMall_SubCategoryIndex^10 MacMall_CategoryIndex^8 MacMall_FacetTemplate^6 MacMall_FacetTemplateName^8 MacMall_Class^0 MacMall_SubClass^0 SearchableAttribute^0 spell^0' where RELEVANCY_ID='0064dYW0V0PzMj1TXaWI' AND field_name='qf';
update [ED_SM].[dbo].[RELEVANCY_FIELDS] set field_value='MacMall_FacetTemplateName^8 MacMall_FacetTemplate^6 UPC^2 MacMall_ManufacturerIndex^8 MacMall_FacetTemplateNameIndex^8 MfrPN^6 MfrPN2^6 MacMall_Name^1.1 RelevantIDKey^4 UPC2^0 MacMall_Description^0 DPNo^0 EDP^0 MacMall_SubCategoryIndex^10 MacMall_CategoryIndex^8' where RELEVANCY_ID='0064dYW0V0PzMj1TXaWI' AND field_name='pf';
update [ED_SM2].[dbo].[RELEVANCY_FIELDS] set field_value='GenericUser_Keywords^0 MacMall_Description^0 UPC^2 MacMall_ManufacturerIndex^8 MacMall_FacetTemplateNameIndex^8 MfrPN^6 MfrPN2^6 MacMall_Name^2 RelevantIDKey^4 UPC2^0 DPNo^0 EDP^0 MacMall_SubCategoryIndex^10 MacMall_CategoryIndex^8 MacMall_FacetTemplate^6 MacMall_FacetTemplateName^8 MacMall_Class^0 MacMall_SubClass^0 SearchableAttribute^0 spell^0' where RELEVANCY_ID='0064dYW0V0PzMj1TXaWI' AND field_name='qf';
update [ED_SM2].[dbo].[RELEVANCY_FIELDS] set field_value='MacMall_FacetTemplateName^8 MacMall_FacetTemplate^6 UPC^2 MacMall_ManufacturerIndex^8 MacMall_FacetTemplateNameIndex^8 MfrPN^6 MfrPN2^6 MacMall_Name^1.1 RelevantIDKey^4 UPC2^0 MacMall_Description^0 DPNo^0 EDP^0 MacMall_SubCategoryIndex^10 MacMall_CategoryIndex^8' where RELEVANCY_ID='0064dYW0V0PzMj1TXaWI' AND field_name='pf';

--pcmg 0064dYW0V0Q1NHB0GrTz
--select * from [ED_SM].[dbo].[relevancy_fields]   WHERE RELEVANCY_ID='0064dYW0V0Q1NHB0GrTz';
update [ED_SM].[dbo].[RELEVANCY_FIELDS] set field_value='GenericUser_Keywords^0 PCMG_Description^0 PCMG_SubClass^0 PCMG_Class^0 SearchableAttribute^0 spell^0 PCMG_FacetTemplateName^8 PCMG_FacetTemplate^6 PCMG_CategoryIndex^8 PCMG_SubCategoryIndex^10 EDP^0 DPNo^0  UPC2^0 RelevantIDKey^4 PCMG_Name^2 MfrPN2^6 MfrPN^6 PCMG_FacetTemplateNameIndex^8 PCMG_ManufacturerIndex^8 UPC^2' where RELEVANCY_ID='0064dYW0V0Q1NHB0GrTz' AND field_name='qf';
update [ED_SM].[dbo].[RELEVANCY_FIELDS] set field_value='PCMG_FacetTemplateName^8 PCMG_FacetTemplate^6 UPC^2 PCMG_ManufacturerIndex^8 PCMG_FacetTemplateNameIndex^8 MfrPN^6 MfrPN2^6 PCMG_Name^1.1 RelevantIDKey^4 UPC2^0 PCMG_Description^0 DPNo^0 EDP^0 PCMG_SubCategoryIndex^10 PCMG_CategoryIndex^8' where RELEVANCY_ID='0064dYW0V0Q1NHB0GrTz' AND field_name='pf';
update [ED_SM2].[dbo].[RELEVANCY_FIELDS] set field_value='GenericUser_Keywords^0 PCMG_Description^0 PCMG_SubClass^0 PCMG_Class^0 SearchableAttribute^0 spell^0 PCMG_FacetTemplateName^8 PCMG_FacetTemplate^6 PCMG_CategoryIndex^8 PCMG_SubCategoryIndex^10 EDP^0 DPNo^0  UPC2^0 RelevantIDKey^4 PCMG_Name^2 MfrPN2^6 MfrPN^6 PCMG_FacetTemplateNameIndex^8 PCMG_ManufacturerIndex^8 UPC^2' where RELEVANCY_ID='0064dYW0V0Q1NHB0GrTz' AND field_name='qf';
update [ED_SM2].[dbo].[RELEVANCY_FIELDS] set field_value='PCMG_FacetTemplateName^8 PCMG_FacetTemplate^6 UPC^2 PCMG_ManufacturerIndex^8 PCMG_FacetTemplateNameIndex^8 MfrPN^6 MfrPN2^6 PCMG_Name^1.1 RelevantIDKey^4 UPC2^0 PCMG_Description^0 DPNo^0 EDP^0 PCMG_SubCategoryIndex^10 PCMG_CategoryIndex^8' where RELEVANCY_ID='0064dYW0V0Q1NHB0GrTz' AND field_name='pf';




