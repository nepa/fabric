/** 06.01.2012 18:25 */
package fabric.module.typegen.cpp;

import de.uniluebeck.sourcegen.Workspace;
import de.uniluebeck.sourcegen.c.*;

import de.uniluebeck.sourcegen.exceptions.CConflictingModifierException;
import de.uniluebeck.sourcegen.exceptions.CPreProcessorValidationException;
import de.uniluebeck.sourcegen.exceptions.CppDuplicateException;
import de.uniluebeck.sourcegen.exceptions.CDuplicateException;

/**
 * This helper class contains various methods that are being used
 * to create C++ functions for char* variables that represent
 * numerical values. This is the case, for example, for xsd:decimal
 * elements that are mapped to char* objects.
 *
 * So we create a header file as well as the cpp file with C++
 * functions (e.g. a compare function). The header can be included
 * in type classes that are generated by the C++ type generator
 * later on.
 *
 * @author reichart
 */
public class CppUtilHelper {
    /** Name of the C++ header file */
    public static final String FILE_NAME = "fabric_util";

    /** Header file for functions for char* variables */
    private static CppHeaderFile headerFile;

    /** Cpp file for functions for char* variables */
    private static CppSourceFile sourceFile;

    /** Cpp namespace */
    private static CppNamespace namespace;

    /**
     * Private constructor, because all methods of this class are static.
     */
    private CppUtilHelper()
    {
        // Empty implementation
    }

    /**
     * Create a header file in the workspace and write type definitions
     * and structs for all required XSD built-in types to it.
     *
     * @param workspace Workspace object for code write-out
     *
     * @throws Exception Error during code generation
     */
    public static void init(Workspace workspace) throws Exception
    {
        CppUtilHelper.createNamespace();
        CppUtilHelper.createHeader(workspace);
        CppUtilHelper.createCpp(workspace);
        CppUtilHelper.createCheckFunctions();
        CppUtilHelper.createStringFunctions();
        CppUtilHelper.createCompareFunctions();
        CppUtilHelper.createDigitFunctions();
        CppUtilHelper.createWhitespaceFunctions();
    }

    private static void createNamespace() {
        namespace = CppNamespace.factory.create(FILE_NAME);
        namespace.setComment(new CCommentImpl(
                "This namespace contains various functions for char* variables representing numerical values."));
    }

    private static void createCpp(Workspace workspace) throws CPreProcessorValidationException, CppDuplicateException {
        // Create Cpp file
        sourceFile = workspace.getC().getCppSourceFile(CppUtilHelper.FILE_NAME);
        sourceFile.setComment(new CCommentImpl("Functions for char* variables representing numerical values."));
        sourceFile.addInclude(headerFile);
        sourceFile.addBeforeDirective("define NULL 0");
    }

    private static void createHeader(Workspace workspace) throws Exception {
        // Create header file
        headerFile = workspace.getC().getCppHeaderFile(CppUtilHelper.FILE_NAME);
        headerFile.setComment(new CCommentImpl("Functions for char* variables representing numerical values."));

        // Surround definitions with include guard
        headerFile.addBeforeDirective("ifndef FABRIC_UTIL_HPP");
        headerFile.addBeforeDirective("define FABRIC_UTIL_HPP");

        // Create type definitions and structs
        headerFile.add(namespace);

        // Close include guard
        headerFile.addAfterDirective("endif // FABRIC_UTIL_HPP");
    }

    private static void createCheckFunctions()
            throws CppDuplicateException, CDuplicateException, CConflictingModifierException {
        // Generate function check_digit
        CParam char_c       = CParam.factory.create("char", "c");
        CFunSignature sig1  = CFunSignature.factory.create(char_c);
        CFun fun_chk_digit  = CFun.factory.create("check_digit", "signed char", sig1);
        String methodBody1  = "return (c>='0') && (c<='9');";
        fun_chk_digit.appendCode(methodBody1);
        fun_chk_digit.setComment(new CCommentImpl("Checks if the given character is a digit."));
        namespace.add(Cpp.PUBLIC, fun_chk_digit);

        // Generate function check_space
        CFunSignature sig2  = CFunSignature.factory.create(char_c);
        CFun fun_chk_space  = CFun.factory.create("check_space", "signed char", sig2);
        String methodBody2  = "return (c==' ' || c=='\\t' || c=='\\n' || c=='\\v' || c=='\\f' || c=='\\r');";
        fun_chk_space.appendCode(methodBody2);
        fun_chk_space.setComment(new CCommentImpl("Checks if the given character is a white-space character."));
        namespace.add(Cpp.PUBLIC, fun_chk_space);
    }

    private static void createStringFunctions()
            throws CppDuplicateException, CConflictingModifierException, CDuplicateException {
        // Generate function my_strchr
        CParam const_char_s = CParam.factory.create("const char *", "s");
        CParam int_c        = CParam.factory.create("int", "c");
        CFunSignature sig1  = CFunSignature.factory.create(const_char_s, int_c);
        CFun fun_my_strchr  = CFun.factory.create("my_strchr", "char *", sig1);
        String methodBody1  =
                "/* Scan s for the character. When this loop is finished,\n" +
                "s will either point to the end of the string or the\n" +
                "character we were looking for. */\n" +
                "while (*s != '\\0' && *s != (char)c)\n" +
                "\ts++;\n" +
                "return ( (*s == c) ? (char *) s : NULL );";
        fun_my_strchr.appendCode(methodBody1);
        fun_my_strchr.setComment(new CCommentImpl("Implementation of the function strchr in string.h"));
        namespace.add(Cpp.PUBLIC, fun_my_strchr);

        // Generate function my_strspn
        CParam const_char_s1    = CParam.factory.create("const char *", "s1");
        CParam const_char_s2    = CParam.factory.create("const char *", "s2");
        CFunSignature sig2      = CFunSignature.factory.create(const_char_s1, const_char_s2);
        CFun fun_my_strspn      = CFun.factory.create("my_strspn", "long", sig2);
        String methodBody2      =
                "const char *sc1;\n" +
                "for (sc1 = s1; *sc1 != '\\0'; sc1++)\n" +
                "\tif (my_strchr(s2, *sc1) == NULL)\n" +
                "\t\treturn (sc1 - s1);\n" +
                "return sc1 - s1; // terminating nulls don't match";
        fun_my_strspn.appendCode(methodBody2);
        fun_my_strspn.setComment(new CCommentImpl("Implementation of the function strspn in string.h"));
        namespace.add(Cpp.PUBLIC, fun_my_strspn);

        // Generate function my_strcspn
        CFun fun_my_strcspn = CFun.factory.create("my_strcspn", "long", sig2);
        String methodBody3  =
                "const char *sc1;\n" +
                "for (sc1 = s1; *sc1 != '\\0'; sc1++)\n" +
                "\tif (my_strchr(s2, *sc1) != NULL)\n" +
                "\t\treturn (sc1 - s1);\n" +
                "return sc1 - s1; // terminating nulls match";
        fun_my_strcspn.appendCode(methodBody3);
        fun_my_strcspn.setComment(new CCommentImpl("Implementation of the function strcspn in string.h"));
        namespace.add(Cpp.PUBLIC, fun_my_strcspn);

        // Generate function my_strtok_r
        CParam char_s           = CParam.factory.create("char *", "s");
        CParam const_char_del   = CParam.factory.create("const char *", "delimiters");
        CParam char_lasts       = CParam.factory.create("char **", "lasts");
        CFunSignature sig3      = CFunSignature.factory.create(char_s, const_char_del, char_lasts);
        CFun fun_my_strtok_r    = CFun.factory.create("my_strtok_r", "char *", sig3);
        String methodBody4      =
                "char *sbegin, *send;\n" +
                "sbegin = s ? s : *lasts;\n" +
                "sbegin += my_strspn(sbegin, delimiters);\n" +
                "if (*sbegin == '\\0') {\n" +
                "\t*lasts = \"\";\n" +
                "\treturn NULL;\n" +
                "}\n" +
                "send = sbegin + my_strcspn(sbegin, delimiters);\n" +
                "if (*send != '\\0')\n" +
                "*send++ = '\\0';\n" +
                "*lasts = send;\n" +
                "return sbegin;";
        fun_my_strtok_r.appendCode(methodBody4);
        fun_my_strtok_r.setComment(new CCommentImpl("Recursive help function for my_strtok"));
        namespace.add(Cpp.PUBLIC, fun_my_strtok_r);

        // Generate function my_strtok
        CFunSignature sig4  = CFunSignature.factory.create(char_s, const_char_del);
        CFun fun_my_strtok  = CFun.factory.create("my_strtok", "char *", sig4);
        String methodBody5  =
                "static char *ssave = \"\";\n" +
                "return my_strtok_r(s, delimiters, &ssave);";
        fun_my_strtok.appendCode(methodBody5);
        fun_my_strtok.setComment(new CCommentImpl("Implementation of the function strtok in string.h"));
        namespace.add(Cpp.PUBLIC, fun_my_strtok);

        // Generate function my_strlen
        CFunSignature sig5  = CFunSignature.factory.create(const_char_s);
        CFun fun_my_strlen  = CFun.factory.create("my_strlen", "int", sig5);
        String methodBody6  =
                "const char *p = s;\n" +
                "/* Loop over the data in s. */\n" +
                "while (*p != '\\0')\n" +
                "\tp++;\n" +
                "return (int) (p - s);";
        fun_my_strlen.appendCode(methodBody6);
        fun_my_strlen.setComment(new CCommentImpl("Implementation of the function strlen in string.h"));
        namespace.add(Cpp.PUBLIC, fun_my_strlen);

        // Generate function my_strcmp
        CFun fun_my_strcmp  = CFun.factory.create("my_strcmp", "signed char", sig2);
        String methodBody7  =
                "/* Move s1 and s2 to the first differing characters\n" +
                "in each string, or the ends of the strings if they\n" +
                "are identical. */\n" +
                "while (*s1 != '\\0' && *s1 == *s2) {\n" +
                "\ts1++;\n" +
                "\ts2++;\n" +
                "}\n" +
                "return (*s1 == *s2);";
        fun_my_strcmp.appendCode(methodBody7);
        fun_my_strcmp.setComment(new CCommentImpl("Checks if the given char* values are equal."));
        namespace.add(Cpp.PUBLIC, fun_my_strcmp);

        // Generate function my_strcpy
        CParam char_s1      = CParam.factory.create("char *", "s1");
        CFunSignature sig6  = CFunSignature.factory.create(char_s1, const_char_s2);
        CFun fun_my_strcpy  = CFun.factory.create("my_strcpy", "char *", sig6);
        String methodBody8  =
                "char *dst = s1;\n" +
                "const char *src = s2;\n" +
                "/* Do the copying in a loop.  */\n" +
                "while ((*dst++ = *src++) != '\\0')\n" +
                "\t;  /* The body of this loop is left empty. */\n" +
                "/* Return the destination string.  */\n" +
                "return s1;";
        fun_my_strcpy.appendCode(methodBody8);
        fun_my_strcpy.setComment(new CCommentImpl("Implementation of the function strcpy in string.h"));
        namespace.add(Cpp.PUBLIC, fun_my_strcpy);
    }

    private static void createCompareFunctions()
            throws CppDuplicateException, CConflictingModifierException, CDuplicateException {
        String comment =
                "Compares two %s as char*.\n" +
                "Returns a value greater zero if the first value is greater than the second one.\n" +
                "Returns zero if both values are equal.\n" +
                "Returns a value less zero otherwise.";

        // Generate function compare_integers
        CParam char_value1  = CParam.factory.create("char *", "value1");
        CParam char_value2  = CParam.factory.create("char *", "value2");
        CFunSignature sig1  = CFunSignature.factory.create(char_value1, char_value2);
        CFun fun_cmp_int    = CFun.factory.create("compare_integers", "signed char", sig1);
        String methodBody1  =
                "// Remove leading zeros\n" +
                "while(*value1 == '0') {\n" +
                "\tvalue1++;\n" +
                "}\n" +
                "while(*value2 == '0') {\n" +
                "\tvalue2++;\n" +
                "}\n\n" +
                "if (my_strlen(value1) == my_strlen(value2)) {\n" +
                "\treturn my_strcmp(value1, value2);\n" +
                "} else {\n" +
                "\treturn (my_strlen(value1) > my_strlen(value2) ? 1 : -1);\n" +
                "}";
        fun_cmp_int.appendCode(methodBody1);
        fun_cmp_int.setComment(new CCommentImpl(String.format(comment, "integers")));
        namespace.add(Cpp.PUBLIC, fun_cmp_int);

        // Generate function compare_decimal_points
        CFun fun_cmp_dec_points = CFun.factory.create("compare_decimal_points", "signed char", sig1);
        String methodBody2      =
                "// Remove trailing zeros\n" +
                "int i;\n" +
                "for (i = my_strlen(value1); i >= 0 && value1[i] == '0'; i--) {\n" +
                "\tvalue1[i] = '\\0';\n" +
                "}\n" +
                "for (i = my_strlen(value2); i >= 0 && value2[i] == '0'; i--) {\n" +
                "\tvalue2[i] = '\\0';\n" +
                "}\n" +
                "return my_strcmp(value1, value2);";
        fun_cmp_dec_points.appendCode(methodBody2);
        fun_cmp_dec_points.setComment(new CCommentImpl(String.format(comment, "decimal point values")));
        namespace.add(Cpp.PUBLIC, fun_cmp_dec_points);

        // Generate function compare_unsigned
        CFun fun_cmp_unsigned   = CFun.factory.create("compare_unsigned", "signed char", sig1);
        String methodBody3      =
                "// Find whole and fraction part of value1\n" +
                "char* wholePart1 = my_strtok(value1, \".\");\n" +
                "char* fractionPart1 = my_strtok(NULL, \".\");\n\n" +
                "// Find whole and fraction part of value2\n" +
                "char* wholePart2 = my_strtok(value2, \".\");\n" +
                "char* fractionPart2 = my_strtok(NULL, \".\");\n\n" +
                "// Compare whole parts\n" +
                "int ret = compare_integers(wholePart1, wholePart2);\n" +
                "if (ret != 0) {\n" +
                "\treturn ret;\n" +
                "} else {\n" +
                "\treturn compare_decimal_points(\n" +
                "\t\tfractionPart1 == NULL ? const_cast<char*>(\"\") : fractionPart1,\n" +
                "\t\tfractionPart2 == NULL ? const_cast<char*>(\"\") : fractionPart2);\n" +
                "}";
        fun_cmp_unsigned.appendCode(methodBody3);
        fun_cmp_unsigned.setComment(new CCommentImpl(String.format(comment, "unsigned numbers")));
        namespace.add(Cpp.PUBLIC, fun_cmp_unsigned);

        // Generate function compare
        CParam const_char_value1    = CParam.factory.create("const char *", "value1");
        CParam const_char_value2    = CParam.factory.create("const char *", "value2");
        CFunSignature sig2          = CFunSignature.factory.create(const_char_value1, const_char_value2);
        CFun fun_cmp                = CFun.factory.create("compare", "signed char", sig2);
        String methodBody4          =
                "char tmp1[my_strlen(value1)];\n" +
                "char* pointer1 = tmp1;\n" +
                "my_strcpy(tmp1, value1);\n\n" +
                "char tmp2[my_strlen(value2)];\n" +
                "char* pointer2 = tmp2;\n" +
                "my_strcpy(tmp2, value2);\n\n" +
                "// value1 < 0\n" +
                "if (*value1 == '-') {\n" +
                "\t// value2 < 0\n" +
                "\tif (*value2 == '-') {\n" +
                "\t\treturn - compare_unsigned(++pointer1, ++pointer2);\n" +
                "\t}\n" +
                "\t// value2 >= 0\n" +
                "\telse {\n" +
                "\t\treturn -1;\n" +
                "\t}\n" +
                "}\n" +
                "// value1 >= 0\n" +
                "else {\n" +
                "\t// value2 < 0\n" +
                "\tif (*value2 == '-') {\n" +
                "\t\treturn 1;\n" +
                "\t}\n" +
                "\t// value2 >= 0\n" +
                "\telse {\n" +
                "\t\treturn compare_unsigned(\n" +
                "\t\t\t*pointer1 == '+' ? ++pointer1 : pointer1,\n" +
                "\t\t\t*pointer2 == '+' ? ++pointer2 : pointer2);\n" +
                "\t}\n" +
                "}";
        fun_cmp.appendCode(methodBody4);
        fun_cmp.setComment(new CCommentImpl(String.format(comment, "numbers")));
        namespace.add(Cpp.PUBLIC, fun_cmp);
    }

    private static void createDigitFunctions()
            throws CppDuplicateException, CConflictingModifierException, CDuplicateException {
        // Generate function total_digits
        CParam const_char_value = CParam.factory.create("const char *", "value");
        CFunSignature sig       = CFunSignature.factory.create(const_char_value);
        CFun fun_total_digits   = CFun.factory.create("total_digits", "int", sig);
        String methodBody1      =
                "int count = 0;\n" +
                "while (*value) {\n" +
                "\tif (check_digit(*value++) > 0) {\n" +
                "\t\tcount++;\n" +
                "\t}\n" +
                "}\n" +
                "return count;";
        fun_total_digits.appendCode(methodBody1);
        fun_total_digits.setComment(new CCommentImpl("Returns the number of digits of the given value."));
        namespace.add(Cpp.PUBLIC, fun_total_digits);

        // Generate function fraction_digits
        CFun fun_frac_digits    = CFun.factory.create("fraction_digits", "int", sig);
        String methodBody2      =
                "char tmp[my_strlen(value)];\n" +
                "my_strcpy(tmp, value);\n" +
                "my_strtok(tmp, \".\");\n" +
                "char* decimalPoints = my_strtok(NULL, \".\");\n" +
                "return decimalPoints == NULL ? 0 : total_digits(decimalPoints);";
        fun_frac_digits.appendCode(methodBody2);
        fun_frac_digits.setComment(new CCommentImpl("Returns the number of decimal points of the given value."));
        namespace.add(Cpp.PUBLIC, fun_frac_digits);
    }

    private static void createWhitespaceFunctions()
            throws CppDuplicateException, CConflictingModifierException, CDuplicateException {
        // Generate function replace
        CParam char_value   = CParam.factory.create("char *", "value");
        CFunSignature sig   = CFunSignature.factory.create(char_value);
        CFun fun_replace    = CFun.factory.create("replace", "void", sig);
        String methodBody2  =
                "unsigned int i;\n" +
                        "unsigned int length = my_strlen(value);\n" +
                        "for (i=0; i < length; i++) {\n" +
                        "\tif (check_space(value[i])) {\n" +
                        "\t\tvalue[i] = ' ';\n" +
                        "\t}\n" +
                        "}";
        fun_replace.appendCode(methodBody2);
        fun_replace.setComment(new CCommentImpl("Replaces all whitespace characters with a space."));
        namespace.add(Cpp.PUBLIC, fun_replace);

        // Generate function collapse
        CFun fun_collapse   = CFun.factory.create("collapse", "void", sig);
        String methodBody1  =
                "replace(value);\n" +
                "char* pos = value;\n" +
                "unsigned int i = 0;\n" +
                "while (check_space(*pos)) {\n" +
                "\tpos++;\n" +
                "}\n\n" +
                "// Replace multiple whitespaces with single space.\n" +
                "while (*pos) {\n" +
                "\tvalue[i++] = *pos;\n" +
                "\tif (check_space(*pos++)) {\n" +
                "\t\twhile (check_space(*pos)) {\n" +
                "\t\t\tpos++;\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n\n" +
                "// Remove trailing whitespaces.\n" +
                "while (check_space(value[--i])) {\n" +
                "\tvalue[i] = '\\0';\n" +
                "}";
        fun_collapse.appendCode(methodBody1);
        fun_collapse.setComment(new CCommentImpl("Replaces multiple whitespace characters with single space and trims."));
        namespace.add(Cpp.PUBLIC, fun_collapse);
    }
}
