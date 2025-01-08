package saltsheep.ssl.core;

public class DescriptorUtils {
    public static String getMethodDescriptor(String returnType, String... parameterTypes) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (String eachParam : parameterTypes)
            builder.append(getFieldDescriptor(eachParam));
        builder.append(')').append(getFieldDescriptor(returnType));
        return builder.toString();
    }

    public static String getFieldDescriptor(String fieldType) {
        switch (fieldType) {
            case "void":
                return "V";
            case "null":
                return "V";
            case "byte":
                return "B";
            case "char":
                return "C";
            case "double":
                return "D";
            case "float":
                return "F";
            case "int":
                return "I";
            case "long":
                return "J";
            case "short":
                return "S";
            case "boolean":
                return "Z";
        }
        if (fieldType.startsWith("[") || fieldType.endsWith("]")) {
            char[] second = fieldType.toCharArray();
            int dimentionCount = 0;
            String innerType = "java.lang.Object";
            if (fieldType.startsWith("[")) {
                for (; dimentionCount < second.length &&
                        second[dimentionCount] == '['; dimentionCount++)
                    ;

                innerType = fieldType.substring(dimentionCount);
            } else if (fieldType.endsWith("]")) {
                for (int j = second.length - 1; j >= 0 &&
                        second[j] == ']'; j -= 2) {
                    dimentionCount++;
                }


                innerType = fieldType.substring(0, second.length - dimentionCount * 2);
            }
            String desc = getFieldDescriptor(innerType);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < dimentionCount; i++)
                builder.append('[');
            builder.append(desc);
            return builder.toString();
        }
        return "L" + fieldType.replace('.', '/') + ";";
    }

    public static String ct(String className) {
        return className.replace('.', '/');
    }
}