package vektah.rust.psi.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import vektah.rust.psi.RustIdent;

public class RustPsiImplUtil {
	public static PsiElement getNameIdentifier(@NotNull RustIdent item) {
		return item.getFirstChild();
	}
}
