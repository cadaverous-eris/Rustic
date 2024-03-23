package rustic.common.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtils {
	
	public static boolean isTypeOf(final Type type, final Class<?> clazz) {
        if (Class.class.isInstance(type)) {
            return clazz.isAssignableFrom(Class.class.cast(type));
        }
        if (ParameterizedType.class.isInstance(type)) {
            final ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
            return isTypeOf(parameterizedType.getRawType(), clazz);
        }
        return false;
    }
	
	public static Type[] getGenericParameter(final Type type) {
        if (ParameterizedType.class.isInstance(type)) {
            return ParameterizedType.class.cast(type).getActualTypeArguments();
        }
        if (GenericArrayType.class.isInstance(type)) {
            return getGenericParameter(GenericArrayType.class.cast(type).getGenericComponentType());
        }
        return null;
    }
	
	public static Type getGenericParameter(final Type type, final int index) {
        if (!ParameterizedType.class.isInstance(type)) {
            return null;
        }
        final Type[] genericParameter = getGenericParameter(type);
        if (genericParameter == null) {
            return null;
        }
        return genericParameter[index];
    }

}
