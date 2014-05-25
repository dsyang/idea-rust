# Thist must be intellijs build of jflex as found in the community edition source tree.
GRAMMAR_KIT_DIR=$(shell echo ~/projects/Grammar-Kit)
IDEA_HOME=/opt/idea
RUSTC=~/projects/rust/x86_64-unknown-linux-gnu/stage2/bin/rustc

JFLEX_BIN=java -jar JFlex.jar
GRAMMAR_KIT_JAR=grammar-kit.jar
IDEA_LIB=$(IDEA_HOME)/lib

SAMPLES=$(shell find src/rust -type f -iname "*.rs")

default: grammar lexer

grammar: src/java/gen/vektah/rust/RustParser.java
src/java/gen/vektah/rust/RustParser.java: src/bnf/RustGrammar.bnf
	java -cp '$(GRAMMAR_KIT_JAR):$(IDEA_LIB)/*:out/production/idea-rust' org.intellij.grammar.Main gen src/bnf/RustGrammar.bnf

lexer: gen/vektah/rust/RustLexer.java grammar
gen/vektah/rust/RustLexer.java: src/flex/RustLexer.flex
	$(JFLEX_BIN) -d gen/vektah/rust --nobak -charat --skel idea-flex.skeleton src/flex/RustLexer.flex

clean:
	rm -rf gen
	$(MAKE) grammar lexer

test: verify_samples


# Ensure all rust samples compile cleanly
verify_samples: $(SAMPLES)
	@for sample in $(SAMPLES) ; do \
		echo VERIFY $$sample ; \
		$(RUSTC) $$sample -o /tmp/rust_sample; \
	done

.PHONY: grammar lexer test
