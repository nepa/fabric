package fabric.core;

import fabric.core.filegen.java.*;
import de.uniluebeck.itm.tr.util.FileUtils;
import de.uniluebeck.sourcegen.SourceFile;
import fabric.Main;
import de.uniluebeck.sourcegen.Workspace;
import fabric.module.typegen.FabricTypeGenModule;
import org.junit.*;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

public class XSD2JavaTest {
    /**
     * Path to resources folder
     */
    private static final String RESOURCES = "src/test/resources/";

    /**
     * Path to schemas folder
     */
    private static final String SCHEMAS = RESOURCES + "schemas/";

    /**
     * Output directory for generated code
     */
    private static final String OUTPUT_DIR = "target";

    /**
     * Properties file
     */
    private static final String PROPERTIES = RESOURCES + "javaTypeGen.properties";

    /**
     * Parameters for using the Type-Generator in the test cases.
     */
    private static final String USE_TYPE_GEN = "typegen";

    /**
     * File format endings of the test files.
     */
    private static final String ENDING_XSD   = ".xsd";

    /**
     * Names of the predefined test files without file format endings.
     */
    private static final String CT_ALL                  = "complexType_all";
    private static final String CT_ANY                  = "complexType_any";
    private static final String CT_ANYATTRIBUTE         = "complexType_anyAttribute";
    private static final String CT_ATTRIBUTES           = "complexType_attributes";
    private static final String CT_CHOICE               = "complexType_choice";
    private static final String CT_COMPLEXCONTENT       = "complexType_complexContent";
    private static final String CT_INNERCOMPLEXTYPE     = "complexType_innerComplexType";
    private static final String CT_REF                  = "complexType_ref";
    private static final String CT_SEQUENCE_GLOBAL      = "complexType_sequence_global";
    private static final String CT_SEQUENCE_LOCAL       = "complexType_sequence_local";
    private static final String CT_SIMPLECONTENT        = "complexType_simpleContent";
    private static final String ST_DIGITS               = "simpleType_digits";
    private static final String ST_ENUMERATION_GLOBAL   = "simpleType_enumeration_global";
    private static final String ST_ENUMERATION_LOCAL    = "simpleType_enumeration_local";
    private static final String ST_INCLUSIVEEXCLUSIVE   = "simpleType_inclusiveExclusive";
    private static final String ST_LENGTH               = "simpleType_length";
    private static final String ST_LIST                 = "simpleType_list";
    private static final String ST_OCCURENCEINDICATORS  = "simpleType_occurenceIndicators";
    private static final String ST_PATTERN              = "simpleType_pattern";
    private static final String ST_SUBSTITUTION         = "simpleType_substitution";
    private static final String ST_VALUES               = "simpleType_values";
    private static final String ST_WHITESPACE           = "simpleType_whiteSpace";
    private static final String ST                      = "simpleTypes";

    /**
     * Workspace
     */
    private static Workspace workspace;

    /**
     * Properties
     */
    private static Properties properties;

    @BeforeClass
    public static void setUp() throws Exception {
        FileInputStream propInFile = new FileInputStream(PROPERTIES);
        properties = new Properties();
        properties.load(propInFile);
    }

    @After
    public void wipeWorkspace() {
        workspace.getSourceFiles().clear();
    }

    @AfterClass
    public static void tearDown() {
        FileUtils.deleteDirectory(new File(properties.getProperty(FabricTypeGenModule.MAIN_CLASS_NAME_KEY)));
    }

    @Test
    public void test_CT_all() throws Exception {
        assertTrue(
                testFile(CT_ALL, new CT_All_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_CT_any() throws Exception {
        assertTrue(
                testFile(CT_ANY, new CT_Any_SourceFileGenerator(properties))
        ); // TODO: Test will fail because there is no support of xs:any yet!
    }

    @Test
    public void test_CT_anyAttribute() throws Exception {
        assertTrue(
                testFile(CT_ANYATTRIBUTE, new CT_AnyAttribute_SourceFileGenerator(properties))
        ); // TODO: Test will fail because there is no support of xs:anyAttribute yet!
    }

    @Test
    public void test_CT_attributes() throws Exception {
        assertTrue(
                testFile(CT_ATTRIBUTES, new CT_Attributes_SourceFileGenerator(properties))
        ); // TODO: Test will fail because there is no support of xs:attribute yet!
    }

    @Test
    public void test_CT_choice() throws Exception {
        assertTrue(
                testFile(CT_CHOICE, new CT_Choice_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_CT_complexContent() throws Exception {
        assertTrue(
                testFile(CT_COMPLEXCONTENT, new CT_ComplexContent_SourceFileGenerator(properties))
        ); // TODO: Test will fail because there is no support of xs:complexContent yet!
    }

    @Test
    public void test_CT_innerComplexType() throws Exception {
        assertTrue(
                testFile(CT_INNERCOMPLEXTYPE, new CT_InnerComplexType_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_CT_ref() throws Exception {
        assertTrue(
                testFile(CT_REF, new CT_Ref_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_CT_sequence_global() throws Exception {
        assertTrue(
                testFile(CT_SEQUENCE_GLOBAL, new CT_Sequence_Global_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_CT_sequence_local() throws Exception {
        assertTrue(
                testFile(CT_SEQUENCE_LOCAL, new CT_Sequence_Local_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_CT_simpleContent() throws Exception {
        assertTrue(
                testFile(CT_SIMPLECONTENT, new CT_SimpleContent_SourceFileGenerator(properties))
        ); // TODO: Test will fail because there is no support of xs:simpleContent yet!
    }

    @Test
    public void test_ST_digits() throws Exception {
        assertTrue(
                testFile(ST_DIGITS, new ST_Digits_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_enumeration_global() throws Exception {
        assertTrue(
                testFile(ST_ENUMERATION_GLOBAL, new ST_Enumeration_Global_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_enumeration_local() throws Exception {
        assertTrue(
                testFile(ST_ENUMERATION_LOCAL, new ST_Enumeration_Local_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_inclusiveExclusive() throws Exception {
        assertTrue(
                testFile(ST_INCLUSIVEEXCLUSIVE, new ST_InclusiveExclusive_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_length() throws Exception {
        assertTrue(
                testFile(ST_LENGTH, new ST_Length_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_list() throws Exception {
        assertTrue(
                testFile(ST_LIST, new ST_List_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_occurenceIndicators() throws Exception {
        assertTrue(
                testFile(ST_OCCURENCEINDICATORS, new ST_OccurenceIndicators_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_pattern() throws Exception {
        assertTrue(
                testFile(ST_PATTERN, new ST_Pattern_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST() throws Exception {
        assertTrue(
                testFile(ST, new ST_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_substitution() throws Exception {
        assertTrue(
                testFile(ST_SUBSTITUTION, new ST_Substitution_SourceFileGenerator(properties))
        ); // TODO: Test will fail because there is no support of substitution yet!
    }

    @Test
    public void test_ST_values() throws Exception {
        assertTrue(
                testFile(ST_VALUES, new ST_Values_SourceFileGenerator(properties))
        );
    }

    @Test
    public void test_ST_whiteSpace() throws Exception {
        assertTrue(
                testFile(ST_WHITESPACE, new ST_WhiteSpace_SourceFileGenerator(properties))
        );
    }

    /**
     * Takes an XSD file and the corresponding JSourceFileGenerator object and tests if the manually
     * generated source files match the automatically generated ones.
     *
     * @param xsd Name of the XSD file to test
     * @param sourceFileGenerator JSourceFileGenerator object corresponding to the given XSD
     * @return true, if the automatically and manually generated source files match, false otherwise.
     */
    private boolean testFile(String xsd, JSourceFileGenerator sourceFileGenerator) {
        /*
         Generate SourceFiles automatically.
         */
        List<SourceFile> sourceFilesAuto = generateSourceFilesAutomatically(xsd);

        /*
         Generate SourceFiles manually.
         */
        List<SourceFile> sourceFilesMan = sourceFileGenerator.getSourceFiles();

        System.out.println("**********************************************************************");
        System.out.println(" UNIT TEST " +xsd);
        System.out.println("**********************************************************************");
        System.out.println("Generated files auto/manu: " + sourceFilesAuto.size() + "/" +sourceFilesMan.size());

        for (SourceFile sourceFile : sourceFilesAuto) {
          System.out.println("Automatically generated file: " + sourceFile.getFileName());
        }

        for (SourceFile sourceFile : sourceFilesMan) {
          System.out.println("Manually generated file: " + sourceFile.getFileName());
        }
        System.out.println("**********************************************************************");

        /*
         True if all source files exist and if there are not too many source files
         */
        return sourceFilesAuto.containsAll(sourceFilesMan) && sourceFilesMan.containsAll(sourceFilesAuto);
    }

    /**
     * Calls the main method with the given XSD and the correct parameters
     * for usage of the type generator.
     *
     * @param xsd Name of the XSD file (without ending ".xsd")
     */
    private List<SourceFile> generateSourceFilesAutomatically(String xsd) {
        List<SourceFile> ret = null;
        String[] params = {"-x", SCHEMAS + xsd + ENDING_XSD, "-p", PROPERTIES, "-m", USE_TYPE_GEN, "-o", OUTPUT_DIR, "-v"};
        Main core = new Main(params);

        Class c = core.getClass();
        Field refWorkspace = null;

        try {
            refWorkspace = c.getDeclaredField("workspace");
            refWorkspace.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {
            Object objWorkspace = refWorkspace.get(core);
            workspace = (Workspace) objWorkspace;
            ret = workspace.getSourceFiles();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
