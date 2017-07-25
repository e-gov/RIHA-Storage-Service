package ee.ria.riha.storage.util;

import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PageableArgumentResolver;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents query parameters of {@link Pageable} type resolved by {@link PageableArgumentResolver}
 *
 * @author Valentin Suhnjov
 * @see ApiImplicitParam
 * @see ApiImplicitParams
 */
@ApiImplicitParams(value = {
        @ApiImplicitParam(name = "page", value = "page number", dataType = "integer", paramType = "query"),
        @ApiImplicitParam(name = "size", value = "page size", dataType = "integer", paramType = "query")
})
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPageableParams {
}
