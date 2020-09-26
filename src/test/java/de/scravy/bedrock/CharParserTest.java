package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.CharParser.*;
import static de.scravy.bedrock.Functions.constant;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class CharParserTest {

  // Fixtures: Brainfuck

  interface BrainfuckCommand {
    enum Simple implements BrainfuckCommand {
      INC,
      DEC,
      NEXT,
      PREV,
      READ,
      WRITE
    }
  }

  @Value
  static class BrainfuckProgram implements BrainfuckCommand {
    private final Seq<BrainfuckCommand> commands;
  }

  private CharParser<BrainfuckProgram> program =
    many(
      right(
        skipMany(noneOf(",.<>+-[]")),
        oneOf(
          loop(),
          character(',').map(constant(BrainfuckCommand.Simple.READ)),
          character('.').map(constant(BrainfuckCommand.Simple.WRITE)),
          character('>').map(constant(BrainfuckCommand.Simple.NEXT)),
          character('<').map(constant(BrainfuckCommand.Simple.PREV)),
          character('+').map(constant(BrainfuckCommand.Simple.INC)),
          character('-').map(constant(BrainfuckCommand.Simple.DEC))
        )
      )
    ).map(BrainfuckProgram::new);

  private CharParser<BrainfuckProgram> loop() {
    return between(
      character('['),
      left(
        recursive(() -> program),
        skipMany(noneOf(",.<>+-[]"))
      ),
      character(']')
    );
  }

  @RequiredArgsConstructor
  final static class BrainfuckVirtualMachine {
    int pointer;
    final int[] memory;
    final IntSupplier input;
    final IntConsumer output;

    public void exec(final BrainfuckCommand.Simple cmd) {
      switch (cmd) {
        case INC:
          memory[pointer]++;
          break;
        case DEC:
          memory[pointer]--;
          break;
        case NEXT:
          pointer++;
          break;
        case PREV:
          pointer--;
          break;
        case READ:
          memory[pointer] = input.getAsInt();
          break;
        case WRITE:
          output.accept(memory[pointer]);
          break;
      }
    }

    public boolean nonZero() {
      return memory[pointer] != 0;
    }
  }

  private void run(final BrainfuckVirtualMachine ctx, final BrainfuckProgram p) {
    for (val cmd : p.getCommands()) {
      if (cmd instanceof BrainfuckCommand.Simple) {
        ctx.exec((BrainfuckCommand.Simple) cmd);
      } else if (cmd instanceof BrainfuckProgram) {
        val pp = (BrainfuckProgram) cmd;
        while (ctx.nonZero()) {
          run(ctx, pp);
        }
      }
    }
  }

  // Fixtures: CSV Parsing

  private final CharParser<Seq<String>> line = sepBy(
    choice(
      between(
        character('"'),
        many(
          choice(
            noneOf("\"\n"),
            string("\"\"").map(constant('"'))
          )
        ),
        character('"')
      ),
      many(noneOf("\",\n"))
    ).map(Seq::asString),
    between(
      skipMany(anyOf(" \t")),
      character(','),
      skipMany(anyOf(" \t"))
    )
  );

  private final CharParser<Seq<Seq<String>>> csv = sepBy(
    line,
    character('\n')
  );

  {
    describe("brainfuck parser", () -> {
      it("should parse the hello world program", () -> {
        val s = "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.";
        val r = program.parse(s);
        expect(r.isSuccess()).toBeTrue();
        val p = r.getValue();
        val mem = new int[10000];
        val out = new StringBuilder();
        val ctx = new BrainfuckVirtualMachine(mem, () -> 0, out::appendCodePoint);
        run(ctx, p);
        expect(out.toString()).toEqual("Hello World!\n");
      });
      it("should parse the 99 bottles of beer program", () -> {
        val s = "[.]++>+>++>>+>>>++++++++++[->+>+>++++++++++<<<]>>>>++++++++++[->+++++>++++++++++>+++++++++++>+++++++" +
          "+>++++++++>+++>++++>+<<<<<<<<]+>--+>+++>++++++>--+>+++++>+++>+++++>+>+>+>+>++>+>+>++[-<]<<<<<<<[->>[>>>>>>" +
          ">>[<<<<<<<[->[-]>>>>>>>>>>>.<----.>>>.<<<--.++.+++.+<-.+<<+<<<<<<<<]+>[-<[-]>>>>>[>>>+<<<<+<+<+>>>-]<<<[->" +
          ">>+<<<]>[>>>>>>+<<<<<<-]>>>>>[[-]>.<]<<<<[>>>>>-<<<<<-]>>[<<+<+<+>>>>-]<<<<[->>>>+<<<<]>[>>>>>>+<<<<<<-]>>" +
          ">>>>.<<<<<[>>>>>-<<<<<-]>>>[-<<<+<+>>>>]<<<<[->>>>+<<<<]>-[[-]>>>>+<<<<]<<<]+>>>>>>>>>>>>>.<<<<----.>----." +
          "+++++..-<++++++++++.-------.<<[[-]>>>.<<<]>>>>>>.<<<----.<+.>>>>.<<<<----.+++..+>+++.+[>]+>+>[->+<<-<-<<<." +
          "<<<----.-.>>>.<<<++++++.<++.---.>>>>.<<<+++.<----.+++++++++++..------>---->-------------------------------" +
          "->>>++.-->..>>>]>>>[->[-]<<<<<<<[<]<[-]>>[>]>>>>>]+>[-<[-]<<<<[->>[->+<<<<-<<<.<<<----.-.>>>.<<<++++++.<++" +
          ".---.>>>>.<<<+++.<----.+++++++++++..------>---->++++++++++++++++++++++++++++++++>>>.<.>>>>>>]<<]<[->>>>[-<" +
          "<+<<<<++.-->.[<]<<<<<<<<[->[-]<]+>[-<[-]>>>>>>>>>>>>>.<<<-----.++++++++++.------.>>>>.<<<----.-.<.>>>>.<<<" +
          "<-.>+.++++++++.---------.>>>.<<<<---.>.<+++.>>>>.<<<++.<---.>+++..>>>.<<<<++++++++.>+.>>>.<<<<--------.>--" +
          ".---.++++++.-------.<+++.++>+++++>>>>.<.[<]<<<<<<<]+>>>>>>-<<<+>>[<<[-]<+<+>>>>-]<<<<[>-<[-]]>[->>>+<<<]>[" +
          "->->+++++++++<<]>>>>>[>]>>>>]<<<<]>>>>>>]+<<<<<<<[<]<]+<+<<<<<<+<-]>>>>>>>>>>[>]>>>>>[->[-]<]+>[-<[-]<<<<<" +
          "<<<<-------------.<<----.>>>.<<<+++++.-----.>>>.<<<+++++.<++.---.>>>>.<<<-.+.-----.+++.<.>>>>.<<<<----.>--" +
          "--.<+++.>>>>.<<<<--.>+++++++.++++.>>>.<<<------.----.--.<+++.>>>>.<<<.++.+++.+<.+>>>>>.<.>>>>>>>>>]+<[-]+<" +
          "[-]<[-]<[-]+<<<[<]<[-]<[-]<[-]<[-]++++++++++[->+>+>++++++++++<<<]>->->-<<<<<<[-]+<[-]<+<<]";
        val r = program.parse(s);
        expect(r.isSuccess()).toBeTrue();
        val p = r.getValue();
        val mem = new int[10000];
        val out = new StringBuilder();
        val ctx = new BrainfuckVirtualMachine(mem, () -> 0, out::appendCodePoint);
        run(ctx, p);
        val a = out.toString().split("\n");
        expect(a[a.length - 1]).toEqual("Go to the store and buy some more, 99 bottles of beer on the wall.");
      });
      it("quine", () -> {
        val s = ">>+++++++>>++>>++++>>+++++++>>+>>++++>>+>>+++>>+>>+++++>>+>>++>>+>>++++++>>++>>++++>>+++++++>>+>>+++" +
          "++>>++>>+>>+>>++++>>+++++++>>+>>+++++>>+>>+>>+>>++++>>+++++++>>+>>+++++>>++++++++++++++>>+>>+>>++++>>+++++" +
          "++>>+>>+++++>>++>>+>>+>>++++>>+++++++>>+>>+++++>>+++++++++++++++++++++++++++++>>+>>+>>++++>>+++++++>>+>>++" +
          "+++>>++>>+>>+>>+++++>>+>>++++++>>+>>++>>+>>++++++>>+>>++>>+>>++++++>>+>>++>>+>>++++++>>+>>++>>+>>++++++>>+" +
          ">>++>>+>>++++++>>+>>++>>+>>++++++>>++>>++++>>+++++++>>+>>+++++>>+++++++>>+>>+++++>>+>>+>>+>>++++>>+>>++>>+" +
          ">>++++++>>+>>+++++>>+++++++>>+>>++++>>+>>+>>++>>+++++>>+>>+++>>+>>++++>>+>>++>>+>>++++++>>+>>+++++>>++++++" +
          "+++++++++++++>>++>>++>>+++>>++>>+>>++>>++++>>+++++++>>++>>+++++>>++++++++++>>+>>++>>++++>>+>>++>>+>>++++++" +
          ">>++++++>>+>>+>>+++++>>+>>++++++>>++>>+++++>>+++++++>>++>>++++>>+>>++++++[<<]>>[>++++++[-<<++++++++++>>]<<" +
          "++..------------------->[-<.>>+<]>[-<+>]>]<<[-[-[-[-[-[-[>++>]<+++++++++++++++++++++++++++++>]<++>]<++++++" +
          "++++++++>]<+>]<++>]<<[->.<]<<]";
        val r = program.parse(s);
        expect(r.isSuccess()).toBeTrue();
        val p = r.getValue();
        val mem = new int[10000];
        val out = new StringBuilder();
        val ctx = new BrainfuckVirtualMachine(mem, () -> 0, out::appendCodePoint);
        run(ctx, p);
        expect(out.toString()).toEqual(s);
      });
      it("search for prime numbers", () -> {
        val s = "compute prime numbers\n" +
          "to use type the max number then push Alt 1 0\n" +
          "===================================================================\n" +
          "======================== OUTPUT STRING ============================\n" +
          "===================================================================\n" +
          ">++++++++[<++++++++>-]<++++++++++++++++.[-]\n" +
          ">++++++++++[<++++++++++>-]<++++++++++++++.[-]\n" +
          ">++++++++++[<++++++++++>-]<+++++.[-]\n" +
          ">++++++++++[<++++++++++>-]<+++++++++.[-]\n" +
          ">++++++++++[<++++++++++>-]<+.[-]\n" +
          ">++++++++++[<++++++++++>-]<+++++++++++++++.[-]\n" +
          ">+++++[<+++++>-]<+++++++.[-]\n" +
          ">++++++++++[<++++++++++>-]<+++++++++++++++++.[-]\n" +
          ">++++++++++[<++++++++++>-]<++++++++++++.[-]\n" +
          ">+++++[<+++++>-]<+++++++.[-]\n" +
          ">++++++++++[<++++++++++>-]<++++++++++++++++.[-]\n" +
          ">++++++++++[<++++++++++>-]<+++++++++++.[-]\n" +
          ">+++++++[<+++++++>-]<+++++++++.[-]\n" +
          ">+++++[<+++++>-]<+++++++.[-]\n" +
          "===================================================================\n" +
          "======================== INPUT NUMBER  ============================\n" +
          "===================================================================\n" +
          "+                          cont=1\n" +
          "[\n" +
          " -                         cont=0\n" +
          " >,\n" +
          " ======SUB10======\n" +
          " ----------\n" +
          " \n" +
          " [                         not 10\n" +
          "  <+>                      cont=1\n" +
          "  =====SUB38======\n" +
          "  ----------\n" +
          "  ----------\n" +
          "  ----------\n" +
          "  --------\n" +
          "  >\n" +
          "  =====MUL10=======\n" +
          "  [>+>+<<-]>>[<<+>>-]<     dup\n" +
          "  >>>+++++++++\n" +
          "  [\n" +
          "   <<<\n" +
          "   [>+>+<<-]>>[<<+>>-]<    dup\n" +
          "   [<<+>>-]\n" +
          "   >>-\n" +
          "  ]\n" +
          "  <<<[-]<\n" +
          "  ======RMOVE1======\n" +
          "  <\n" +
          "  [>+<-]\n" +
          " ]\n" +
          " <\n" +
          "]\n" +
          ">>[<<+>>-]<<\n" +
          "===================================================================\n" +
          "======================= PROCESS NUMBER  ===========================\n" +
          "===================================================================\n" +
          "==== ==== ==== ====\n" +
          "numd numu teid teiu\n" +
          "==== ==== ==== ====\n" +
          ">+<-\n" +
          "[\n" +
          " >+\n" +
          " ======DUP======\n" +
          " [>+>+<<-]>>[<<+>>-]<\n" +
          " >+<--\n" +
          " >>>>>>>>+<<<<<<<<   isprime=1\n" +
          " [\n" +
          "  >+\n" +
          "  <-\n" +
          "  =====DUP3=====\n" +
          "  <[>>>+>+<<<<-]>>>>[<<<<+>>>>-]<<<\n" +
          "  =====DUP2=====\n" +
          "  >[>>+>+<<<-]>>>[<<<+>>>-]<<< <\n" +
          "  >>>\n" +
          "  ====DIVIDES=======\n" +
          "  [>+>+<<-]>>[<<+>>-]<   DUP i=div\n" +
          "  \n" +
          "  <<\n" +
          "  [\n" +
          "    >>>>>+               bool=1\n" +
          "    <<<\n" +
          "    [>+>+<<-]>>[<<+>>-]< DUP\n" +
          "    [>>[-]<<-]           IF i THEN bool=0\n" +
          "    >>\n" +
          "    [                    IF i=0\n" +
          "      <<<<\n" +
          "      [>+>+<<-]>>[<<+>>-]< i=div\n" +
          "      >>>\n" +
          "      -                  bool=0\n" +
          "    ]\n" +
          "    <<<\n" +
          "    -                    DEC i\n" +
          "    <<\n" +
          "    -\n" +
          "  ]\n" +
          "  \n" +
          "  +>>[<<[-]>>-]<<          \n" +
          "  >[-]<                  CLR div\n" +
          "  =====END DIVIDES====\n" +
          "  [>>>>>>[-]<<<<<<-]     if divides then isprime=0\n" +
          "  <<\n" +
          "  >>[-]>[-]<<<\n" +
          " ]\n" +
          " >>>>>>>>\n" +
          " [\n" +
          "  -\n" +
          "  <<<<<<<[-]<<\n" +
          "  [>>+>+<<<-]>>>[<<<+>>>-]<<<\n" +
          "  >>\n" +
          "  ===================================================================\n" +
          "  ======================== OUTPUT NUMBER  ===========================\n" +
          "  ===================================================================\n" +
          "  [>+<-]>\n" +
          " \n" +
          "  [\n" +
          "   ======DUP======\n" +
          "   [>+>+<<-]>>[<<+>>-]<\n" +
          "  \n" +
          "  \n" +
          "   ======MOD10====\n" +
          "   >+++++++++<\n" +
          "   [\n" +
          "    >>>+<<              bool= 1\n" +
          "    [>+>[-]<<-]         bool= ten==0\n" +
          "    >[<+>-]             ten = tmp\n" +
          "    >[<<++++++++++>>-]  if ten=0 ten=10\n" +
          "    <<-                 dec ten     \n" +
          "    <-                  dec num\n" +
          "   ]\n" +
          "   +++++++++            num=9\n" +
          "   >[<->-]<             dec num by ten\n" +
          "  \n" +
          "   =======RROT======\n" +
          "      [>+<-]\n" +
          "   <  [>+<-]\n" +
          "   <  [>+<-]\n" +
          "   >>>[<<<+>>>-]\n" +
          "   <\n" +
          "  \n" +
          "   =======DIV10========\n" +
          "   >+++++++++<\n" +
          "   [\n" +
          "    >>>+<<                bool= 1\n" +
          "    [>+>[-]<<-]           bool= ten==0\n" +
          "    >[<+>-]               ten = tmp\n" +
          "    >[<<++++++++++>>>+<-] if ten=0 ten=10  inc div\n" +
          "    <<-                   dec ten     \n" +
          "    <-                    dec num\n" +
          "   ]\n" +
          "   >>>>[<<<<+>>>>-]<<<<   copy div to num\n" +
          "   >[-]<                  clear ten\n" +
          "  \n" +
          "   =======INC1=========\n" +
          "   <+>\n" +
          "  ]\n" +
          "  \n" +
          "  <\n" +
          "  [\n" +
          "   =======MOVER=========\n" +
          "   [>+<-]\n" +
          "  \n" +
          "   =======ADD48========\n" +
          "   +++++++[<+++++++>-]<->\n" +
          "  \n" +
          "   =======PUTC=======\n" +
          "   <.[-]>\n" +
          "  \n" +
          "   ======MOVEL2========\n" +
          "   >[<<+>>-]<\n" +
          "  \n" +
          "   <-\n" +
          "  ]\n" +
          " \n" +
          "  >++++[<++++++++>-]<.[-]\n" +
          " \n" +
          "  ===================================================================\n" +
          "  =========================== END FOR ===============================\n" +
          "  ===================================================================\n" +
          "  >>>>>>>\n" +
          " ]\n" +
          " <<<<<<<<\n" +
          " >[-]<\n" +
          "  [-]\n" +
          " <<-\n" +
          "]\n" +
          " \n" +
          "======LF========\n" +
          " \n" +
          "++++++++++.[-]\n" +
          "@";
        val r = program.parse(s);
        expect(r.isSuccess()).toBeTrue();
        val p = r.getValue();
        val mem = new int[10000];
        val out = new StringBuilder();
        val in = "20\n";
        val inIx = Box.intBox(-1);
        val ctx = new BrainfuckVirtualMachine(mem, () -> in.charAt(inIx.update(i -> i + 1)), out::appendCodePoint);
        run(ctx, p);
        expect(out.toString().trim()).toEqual("Primes up to: 2 3 5 7 11 13 17 19");
      });
    });

    describe("csv parser", () -> {
      it("should parse stuff", () -> {
        val s = "hello,\"huhu\"\"haha\",yeaha,\"aha\"\"hh\"\",\",aas\n" +
          "\",\",asbc,ojs,,as\n";
        val r = csv.parse(s);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue().take(2)).toEqual(
          Seq.of(
            Seq.of("hello", "huhu\"haha", "yeaha", "aha\"hh\",", "aas"),
            Seq.of(",", "asbc", "ojs", "", "as")
          )
        );
      });
    });

    describe("char parsers", () -> {
      it("times", () -> {
        val s = "foofoobarfoobaz";
        val p = times(4, choice(string("foo"), string("bar")));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.of("foo", "foo", "bar", "foo"));
      });
      it("sequence", () -> {
        val s = "123456789";
        val p = sequence(character('1'), character('2'), character('3'), character('4'), character('5'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue().asString()).toEqual("12345");
        expect(r.getRemaining().asString()).toEqual("6789");
      });
      it("seq/4", () -> {
        val s = "123456789";
        val p = seq(character('1'), character('2'), character('3'), character('4'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue().toSeq().asString()).toEqual("1234");
        expect(r.getRemaining().asString()).toEqual("56789");
      });
      it("seq/3", () -> {
        val s = "123456789";
        val p = seq(character('1'), character('2'), character('3'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue().toSeq().asString()).toEqual("123");
        expect(r.getRemaining().asString()).toEqual("456789");
      });
      it("seq/2", () -> {
        val s = "123456789";
        val p = seq(character('1'), character('2'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue().toSeq().asString()).toEqual("12");
        expect(r.getRemaining().asString()).toEqual("3456789");
      });
      it("seq/4 failure", () -> {
        val s = "0123456789";
        val p = seq(character('1'), character('2'), character('3'), character('4'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("seq/4 failure 2", () -> {
        val s = "1023456789";
        val p = seq(character('1'), character('2'), character('3'), character('4'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("seq/4 failure 3", () -> {
        val s = "1203456789";
        val p = seq(character('1'), character('2'), character('3'), character('4'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("seq/4 failure 4", () -> {
        val s = "1230456789";
        val p = seq(character('1'), character('2'), character('3'), character('4'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("seq/3 failure", () -> {
        val s = "0123456789";
        val p = seq(character('1'), character('2'), character('3'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("seq/3 failure 2", () -> {
        val s = "1023456789";
        val p = seq(character('1'), character('2'), character('3'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("seq/3 failure 3", () -> {
        val s = "1203456789";
        val p = seq(character('1'), character('2'), character('3'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("seq/2 failure", () -> {
        val s = "0123456789";
        val p = seq(character('1'), character('2'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("seq/2 failure 2", () -> {
        val s = "1023456789";
        val p = seq(character('1'), character('2'));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeFalse();
      });
      it("satisfies", () -> {
        val s = "xy";
        val p = many1(satisfies(x -> x.equals('x')));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.of('x'));
      });
      it("satisfies2", () -> {
        val s = "xy";
        val p = many1(satisfies2(Optional::of));
        val r = p.parse(s);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.of('x', 'y'));
      });
    });

  }

}
