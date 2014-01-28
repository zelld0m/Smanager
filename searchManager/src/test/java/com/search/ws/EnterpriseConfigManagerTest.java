package com.search.ws;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context-test.xml")
public class EnterpriseConfigManagerTest {

    @Autowired
    private EnterpriseConfigManager enterpriseConfigManager;
    
    private final static String[] SOLR_CORES = {
        "macmall",
        "ecost",
        "pcmall",
        "pcmallgov",
    };
    
    private final static String[] STORE_IDS = {
        "macmall",                
        "mmoe",                
        "mmrt",
        "ecost",                
        "ecoe",    
        "pcmall",
        "pcoe",
        "pcmallgov",
        "pcmgoe",
        "pcmgfed",
        "pcmgfedoe",
        "pcmgsl",                
        "pcmgsloe",                
        "pcmgedk12",                
        "pcmgedk12oe",                
        "pcmgedhe",                
        "pcmgedheoe"               
    };
    
    private final static String[] STORE_NAMES = {
        "MacMall Web",                
        "MacMall Order Entry",                
        "MacMall Retail",  
        "eCOST",                
        "eCOST Order Entry",
        "PCM",
        "PCM Order Entry",
        "PCMG Open eCommerce",
        "PCMG Open Order Entry",
        "PCMG Federal Government eCommerce",
        "PCMG Federal Government Order Entry",
        "PCMG State/Local Government eCommerce",                
        "PCMG State/Local Government Order Entry",                
        "PCMG K12 Educational eCommerce",                
        "PCMG K12 Educational Order Entry",                
        "PCMG Higher Educational eCommerce",                
        "PCMG Higher Educational Order Entry"              
    };
    
    @Test
    public void testGetAllStoreNames() {
        assertEquals(Arrays.asList(STORE_NAMES), enterpriseConfigManager.getAllStoreNames());
    }

    @Test
    public void testGetAllStoreIds() {
        assertEquals(Arrays.asList(STORE_IDS), enterpriseConfigManager.getAllStoreIds());
    }
    
    @Test
    public void testGetAllCores() {
        assertEquals(Arrays.asList(SOLR_CORES), enterpriseConfigManager.getAllCores());
    }
    
    @Test
    public void testGetStoreParameterValue(){
        assertEquals("linear(MacMall_PopularityScale,1,0)^50", enterpriseConfigManager.getStoreParameterValue("macmall", "/store[@id='%s']/relevancy-default/bf"));
    }
        
        
    @Test
    public void testGetParentStoreIdByAlias(){
        assertEquals("pcmall", enterpriseConfigManager.getParentStoreIdByAlias("pcmall"));
        assertEquals("pcmall", enterpriseConfigManager.getParentStoreIdByAlias("pcoe"));
        
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmallgov"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgoe"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgfed"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgfedoe"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgsl"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgsloe"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgedk12"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgedk12oe"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgedhe"));
        assertEquals("pcmallgov", enterpriseConfigManager.getParentStoreIdByAlias("pcmgedheoe"));
        
        assertEquals("macmall", enterpriseConfigManager.getParentStoreIdByAlias("macmall"));
        assertEquals("macmall", enterpriseConfigManager.getParentStoreIdByAlias("mmoe"));
        assertEquals("macmall", enterpriseConfigManager.getParentStoreIdByAlias("mmrt"));
        
        assertEquals("ecost", enterpriseConfigManager.getParentStoreIdByAlias("ecost"));
        assertEquals("ecost", enterpriseConfigManager.getParentStoreIdByAlias("ecoe"));
    }
    
    @Test
    public void testGetDismax() {
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmall"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcoe"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmallgov"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgoe"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgfed"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgfedoe"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgsl"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgsloe"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgedk12"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgedk12oe"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgedhe"));
        assertEquals("dismaxpcmallrelevancy", enterpriseConfigManager.getDismax("pcmgedheoe"));
        
        assertEquals("dismaxmacmallrelevancy", enterpriseConfigManager.getDismax("macmall"));
        assertEquals("dismaxmacmallrelevancy", enterpriseConfigManager.getDismax("mmoe"));
        assertEquals("dismaxmacmallrelevancy", enterpriseConfigManager.getDismax("mmrt"));
        
        assertEquals("dismaxecostrelevancy", enterpriseConfigManager.getDismax("ecost"));
        assertEquals("dismaxecostrelevancy", enterpriseConfigManager.getDismax("ecoe"));
    }
    
    @Test
    public void testGetStoreFlag() {
        assertEquals("PcMall_StoreFlag", enterpriseConfigManager.getStoreFlag("pcmall"));
        assertEquals("PCMall_OrderEntryFlag", enterpriseConfigManager.getStoreFlag("pcoe"));
        assertEquals("PCMG_OpenStoreFlag", enterpriseConfigManager.getStoreFlag("pcmallgov"));
        assertEquals("PCMG_OpenOrderEntryFlag", enterpriseConfigManager.getStoreFlag("pcmgoe"));
        assertEquals("PCMG_FedGovStoreFlag", enterpriseConfigManager.getStoreFlag("pcmgfed"));
        assertEquals("PCMG_FedGovOrderEntryFlag", enterpriseConfigManager.getStoreFlag("pcmgfedoe"));
        assertEquals("PCMG_SNLGovStoreFlag", enterpriseConfigManager.getStoreFlag("pcmgsl"));
        assertEquals("PCMG_SNLGovOrderEntryFlag", enterpriseConfigManager.getStoreFlag("pcmgsloe"));
        assertEquals("PCMG_K12ACAStoreFlag", enterpriseConfigManager.getStoreFlag("pcmgedk12"));
        assertEquals("PCMG_K12ACAOrderEntryFlag", enterpriseConfigManager.getStoreFlag("pcmgedk12oe"));
        assertEquals("PCMG_HEACAStoreFlag", enterpriseConfigManager.getStoreFlag("pcmgedhe"));
        assertEquals("PCMG_HEACAOrderEntryFlag", enterpriseConfigManager.getStoreFlag("pcmgedheoe"));
        
        assertEquals("MacMall_StoreFlag", enterpriseConfigManager.getStoreFlag("macmall"));
        assertEquals("MacMall_OrderEntryFlag", enterpriseConfigManager.getStoreFlag("mmoe"));
        assertEquals("MacMallRetail_StoreFlag", enterpriseConfigManager.getStoreFlag("mmrt"));
        
        assertEquals("eCOST_StoreFlag", enterpriseConfigManager.getStoreFlag("ecost"));
        assertEquals("eCOST_OrderEntryFlag", enterpriseConfigManager.getStoreFlag("ecoe"));
    }
    
    
    @Test
    public void testGetDefType() {
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmall"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcoe"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmallgov"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgoe"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgfed"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgfedoe"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgsl"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgsloe"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgedk12"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgedk12oe"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgedhe"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("pcmgedheoe"));
        
        assertEquals("edismax", enterpriseConfigManager.getDefType("macmall"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("mmoe"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("mmrt"));
        
        assertEquals("edismax", enterpriseConfigManager.getDefType("ecost"));
        assertEquals("edismax", enterpriseConfigManager.getDefType("ecoe"));
    }
    
    @Test
    public void testGetPopularity() {
        assertEquals("PcMall_Popularity", enterpriseConfigManager.getPopularity("pcmall"));
        assertEquals("PcMall_Popularity", enterpriseConfigManager.getPopularity("pcoe"));
        assertEquals("PCMG_Open_Popularity", enterpriseConfigManager.getPopularity("pcmallgov"));
        assertEquals("PCMG_Open_Popularity", enterpriseConfigManager.getPopularity("pcmgoe"));
        assertEquals("PCMG_FedGov_Popularity", enterpriseConfigManager.getPopularity("pcmgfed"));
        assertEquals("PCMG_FedGov_Popularity", enterpriseConfigManager.getPopularity("pcmgfedoe"));
        assertEquals("PCMG_SNLGov_Popularity", enterpriseConfigManager.getPopularity("pcmgsl"));
        assertEquals("PCMG_SNLGov_Popularity", enterpriseConfigManager.getPopularity("pcmgsloe"));
        assertEquals("PCMG_K12ACA_Popularity", enterpriseConfigManager.getPopularity("pcmgedk12"));
        assertEquals("PCMG_K12ACA_Popularity", enterpriseConfigManager.getPopularity("pcmgedk12oe"));
        assertEquals("PCMG_HEACA_Popularity", enterpriseConfigManager.getPopularity("pcmgedhe"));
        assertEquals("PCMG_HEACA_Popularity", enterpriseConfigManager.getPopularity("pcmgedheoe"));
        
        assertEquals("MacMall_Popularity", enterpriseConfigManager.getPopularity("macmall"));
        assertEquals("MacMall_Popularity", enterpriseConfigManager.getPopularity("mmoe"));
        assertEquals("MacMall_Popularity", enterpriseConfigManager.getPopularity("mmrt"));
        
        assertEquals("eCOST_Popularity", enterpriseConfigManager.getPopularity("ecost"));
        assertEquals("eCOST_Popularity", enterpriseConfigManager.getPopularity("ecoe"));
    }

    @Test
    public void testGetFacetTemplate() {
        assertEquals("PCMall_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmall"));
        assertEquals("PCMall_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcoe"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmallgov"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgoe"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgfed"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgfedoe"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgsl"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgsloe"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgedk12"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgedk12oe"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgedhe"));
        assertEquals("PCMG_FacetTemplate", enterpriseConfigManager.getFacetTemplate("pcmgedheoe"));
        
        assertEquals("MacMall_FacetTemplate", enterpriseConfigManager.getFacetTemplate("macmall"));
        assertEquals("MacMall_FacetTemplate", enterpriseConfigManager.getFacetTemplate("mmoe"));
        assertEquals("MacMall_FacetTemplate", enterpriseConfigManager.getFacetTemplate("mmrt"));
        
        assertEquals("eCOST_FacetTemplate", enterpriseConfigManager.getFacetTemplate("ecost"));
        assertEquals("eCOST_FacetTemplate", enterpriseConfigManager.getFacetTemplate("ecoe"));
    }
    
    @Test
    public void testGetProductName() {
        assertEquals("PcMall_Name", enterpriseConfigManager.getProductName("pcmall"));
        assertEquals("PcMall_Name", enterpriseConfigManager.getProductName("pcoe"));
        
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmallgov"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgoe"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgfed"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgfedoe"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgsl"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgsloe"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgedk12"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgedk12oe"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgedhe"));
        assertEquals("PCMG_Name", enterpriseConfigManager.getProductName("pcmgedheoe"));
        
        assertEquals("MacMall_Name", enterpriseConfigManager.getProductName("macmall"));
        assertEquals("MacMall_Name", enterpriseConfigManager.getProductName("mmoe"));
        assertEquals("MacMall_Name", enterpriseConfigManager.getProductName("mmrt"));
        
        assertEquals("eCOST_Name", enterpriseConfigManager.getProductName("ecost"));
        assertEquals("eCOST_Name", enterpriseConfigManager.getProductName("ecoe"));
    }
    
    @Test
    public void testGetProductDescription() {
        assertEquals("PcMall_Description", enterpriseConfigManager.getProductDescription("pcmall"));
        assertEquals("PcMall_Description", enterpriseConfigManager.getProductDescription("pcoe"));
        
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmallgov"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgoe"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgfed"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgfedoe"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgsl"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgsloe"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgedk12"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgedk12oe"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgedhe"));
        assertEquals("PCMG_Description", enterpriseConfigManager.getProductDescription("pcmgedheoe"));
        
        assertEquals("MacMall_Description", enterpriseConfigManager.getProductDescription("macmall"));
        assertEquals("MacMall_Description", enterpriseConfigManager.getProductDescription("mmoe"));
        assertEquals("MacMall_Description", enterpriseConfigManager.getProductDescription("mmrt"));
        
        assertEquals("eCOST_Description", enterpriseConfigManager.getProductDescription("ecost"));
        assertEquals("eCOST_Description", enterpriseConfigManager.getProductDescription("ecoe"));
    }
 }