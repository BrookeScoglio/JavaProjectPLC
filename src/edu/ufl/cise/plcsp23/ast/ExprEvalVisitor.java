package edu.ufl.cise.plcsp23.ast;
import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.IToken.Kind;



//Implement Visitor Interface
public class ExprEvalVisitor implements ASTVisitor {

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException {

    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        int left = (Integer) e.left.visit(this, arg);
        int right = (Integer ) e.right.visit(this,arg);
        Kind opKind = e.op.getKind();
        int val = switch(opKind){
            case PLUS -> left + right;
            case MINUS -> left – right;
            case TIMES -> left * right;
            case DIV -> left/right;
            default -> {…}
        }
        return val;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {

    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException {

    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {

    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {

    }

    @Override
    public Object visitZExpr(ZExpr constExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        return null;
    }
}
