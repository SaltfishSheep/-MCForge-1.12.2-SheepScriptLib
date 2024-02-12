package saltsheep.ssl.puppet.asm;

public class DescriptorUtils {
    public static String getMethodDescriptor(String returnType, String... parameterTypes) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for(String eachParam:parameterTypes)
            builder.append(getFieldDescriptor(eachParam));
        builder.append(')').append(getFieldDescriptor(returnType));
        return builder.toString();
    }

    //*As a Object, input as "java.lang.Object",as a array, input as "[java.lang.Object"([int) or "java.lang.Object[]"(int[]).
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
            default:
                if(fieldType.startsWith("[")||fieldType.endsWith("]")) {//*如果是数组
                    char[] second = fieldType.toCharArray();
                    int dimentionCount = 0;
                    String innerType = "java.lang.Object";
                    if(fieldType.startsWith("[")) {
                        for (; dimentionCount < second.length; dimentionCount++)
                            if (second[dimentionCount] != '[')
                                break;
                        innerType = fieldType.substring(dimentionCount);
                    }else if(fieldType.endsWith("]")){
                        for(int i = second.length-1;i>=0;i-=2){
                            if(second[i]==']')
                                dimentionCount++;
                            else
                                break;
                        }
                        innerType = fieldType.substring(0,second.length-dimentionCount*2);
                    }
                    String desc = getFieldDescriptor(innerType);//*因为也有可能是基础数据类型
                    StringBuilder builder = new StringBuilder();
                    for(int i=0;i<dimentionCount;i++)
                        builder.append('[');
                    builder.append(desc);
                    return builder.toString();
                }else
                    return "L" + fieldType.replace('.', '/') + ";";
        }
    }
}