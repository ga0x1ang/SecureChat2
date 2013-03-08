package common;

/**
 * Created with IntelliJ IDEA.
 * User: Xiang Gao
 * Date: 2/15/13
 * Time: 5:02 PM
 */
public final class Command {
    private String instruction;
    private String[] parameters;

    public Command(final String instr, final String[] params) {
        instruction = instr;
        parameters = params.clone();
    }

    public String getInstruction() {
        return instruction;
    }

    public String[] getParameters() {

        String[] ret = this.parameters;
        return ret;
    }
}
