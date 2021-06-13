package caprita.catalin.cityquest.ui.dagger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scope that will be owned by the MainActivity Module. Everything that is withing hte MainScope,
 * will live as long as the MainActivity lives.*/
@Scope
@Documented
@Retention(RUNTIME)
public @interface MainScope {
}
