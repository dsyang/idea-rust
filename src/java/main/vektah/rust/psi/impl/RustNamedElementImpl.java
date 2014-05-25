package vektah.rust.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import vektah.rust.psi.RustNamedElement;

public abstract class RustNamedElementImpl extends ASTWrapperPsiElement implements RustNamedElement {

	public RustNamedElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		return this;
	}
}
