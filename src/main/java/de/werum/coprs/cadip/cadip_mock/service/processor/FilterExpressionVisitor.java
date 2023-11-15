package de.werum.coprs.cadip.cadip_mock.service.processor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.edm.primitivetype.EdmBoolean;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.commons.core.edm.primitivetype.EdmGuid;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;

import de.werum.coprs.cadip.cadip_mock.util.TimeUtil;

public class FilterExpressionVisitor implements ExpressionVisitor<Object> {

	private static final Logger LOG = LogManager.getLogger(FilterExpressionVisitor.class);
	private Entity currentEntity;

	public FilterExpressionVisitor(Entity currentEntity) {
		this.currentEntity = currentEntity;
	}

	@Override
	public Object visitBinaryOperator(BinaryOperatorKind operator, Object left, List<Object> right)
			throws ExpressionVisitException, ODataApplicationException {
		if (!operator.equals(BinaryOperatorKind.IN)) {
			throw new ODataApplicationException("Binary operation " + operator.name() + " is not implemented",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ENGLISH);
		}
		for (Object rightOperand : right) {
			if ((boolean) visitBinaryOperator(BinaryOperatorKind.EQ, left, rightOperand)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object visitBinaryOperator(BinaryOperatorKind operator, Object left, Object right)
			throws ExpressionVisitException, ODataApplicationException {
		// Binary Operators are split up in three different kinds. Up to the kind of the
		// operator it can be applied to different types
		// - Arithmetic operations like add, minus, modulo, etc. are allowed on numeric
		// types like Edm.Int64
		// - Logical operations are allowed on numeric types and also Edm.String
		// - Boolean operations like and, or are allowed on Edm.Boolean
		// A detailed explanation can be found in OData Version 4.0 Part 2: URL
		// Conventions

		if (operator == BinaryOperatorKind.ADD || operator == BinaryOperatorKind.MOD
				|| operator == BinaryOperatorKind.MUL || operator == BinaryOperatorKind.DIV
				|| operator == BinaryOperatorKind.SUB) {
			return evaluateArithmeticOperation(operator, left, right);
		} else if (operator == BinaryOperatorKind.EQ || operator == BinaryOperatorKind.NE
				|| operator == BinaryOperatorKind.GE || operator == BinaryOperatorKind.GT
				|| operator == BinaryOperatorKind.LE || operator == BinaryOperatorKind.LT) {
			return evaluateComparisonOperation(operator, left, right);
		} else if (operator == BinaryOperatorKind.AND || operator == BinaryOperatorKind.OR) {
			return evaluateBooleanOperation(operator, left, right);
		} else {
			throw new ODataApplicationException("Binary operation " + operator.name() + " is not implemented",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	private Object evaluateBooleanOperation(BinaryOperatorKind operator, Object left, Object right)
			throws ODataApplicationException {

		// First check that both operands are of type Boolean
		if (left instanceof Boolean && right instanceof Boolean) {
			Boolean valueLeft = (Boolean) left;
			Boolean valueRight = (Boolean) right;

			// Than calculate the result value
			if (operator == BinaryOperatorKind.AND) {
				return valueLeft && valueRight;
			} else {
				// OR
				return valueLeft || valueRight;
			}
		} else {
			throw new ODataApplicationException("Boolean operations needs two numeric operands",
					HttpStatusCode.BAD_REQUEST.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	private Object evaluateComparisonOperation(BinaryOperatorKind operator, Object left, Object right)
			throws ODataApplicationException {

		// All types in our tutorial supports all logical operations, but we have to
		// make sure that
		// the types are equal
		LOG.trace("Comparing Entity left: {}: {} - right: {}: {}", left.getClass(), left.toString(), right.getClass(),
				right.toString());
		if (left.getClass().equals(right.getClass())) {
			// Luckily all used types String, Boolean and also Integer support the interface
			// Comparable
			int result;
			if (left instanceof Long) {
				result = ((Long) left).compareTo((Long) right);
			} else if (left instanceof String) {
				result = ((String) left).compareTo((String) right);
			} else if (left instanceof Boolean) {
				result = ((Boolean) left).compareTo((Boolean) right);
			} else if (left instanceof Timestamp) {
				result = ((Timestamp) left).compareTo((Timestamp) right);
			} else if (left instanceof UUID) {
				result = ((UUID) left).compareTo((UUID) right);
			} else {
				throw new ODataApplicationException("Class " + left.getClass().getCanonicalName() + " not expected",
						HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
						Locale.ENGLISH);
			}

			if (operator == BinaryOperatorKind.EQ) {
				return result == 0;
			} else if (operator == BinaryOperatorKind.NE) {
				return result != 0;
			} else if (operator == BinaryOperatorKind.GE) {
				return result >= 0;
			} else if (operator == BinaryOperatorKind.GT) {
				return result > 0;
			} else if (operator == BinaryOperatorKind.LE) {
				return result <= 0;
			} else {
				// BinaryOperatorKind.LT
				return result < 0;
			}

		} else {
			throw new ODataApplicationException("Comparison needs two equal types",
					HttpStatusCode.BAD_REQUEST.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	private Object evaluateArithmeticOperation(BinaryOperatorKind operator, Object left, Object right)
			throws ODataApplicationException {

		// First check if the type of both operands is numerical
		if (left instanceof Long && right instanceof Long) {
			Long valueLeft = (Long) left;
			Long valueRight = (Long) right;

			// Than calculate the result value
			if (operator == BinaryOperatorKind.ADD) {
				return valueLeft + valueRight;
			} else if (operator == BinaryOperatorKind.SUB) {
				return valueLeft - valueRight;
			} else if (operator == BinaryOperatorKind.MUL) {
				return valueLeft * valueRight;
			} else if (operator == BinaryOperatorKind.DIV) {
				return valueLeft / valueRight;
			} else {
				// BinaryOperatorKind,MOD
				return valueLeft % valueRight;
			}
		} else {
			throw new ODataApplicationException("Arithmetic operations needs two numeric operands",
					HttpStatusCode.BAD_REQUEST.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	@Override
	public Object visitUnaryOperator(UnaryOperatorKind operator, Object operand)
			throws ExpressionVisitException, ODataApplicationException {
		// OData allows two different unary operators. We have to take care, that the
		// type of the
		// operand fits to the operand

		if (operator == UnaryOperatorKind.NOT && operand instanceof Boolean) {
			// 1.) boolean negation
			return !(Boolean) operand;
		} else if (operator == UnaryOperatorKind.MINUS && operand instanceof Long) {
			// 2.) arithmetic minus
			return -(Long) operand;
		}

		// Operation not processed, throw an exception
		throw new ODataApplicationException("Invalid type for unary operator",
				HttpStatusCode.BAD_REQUEST.getStatusCode(),
				Locale.ENGLISH);
	}

	@Override
	public Object visitMethodCall(MethodKind methodCall, List<Object> parameters)
			throws ExpressionVisitException, ODataApplicationException {
		// To keep this tutorial small and simple, we implement only one method call
		// contains(String, String) -> Boolean
		if (methodCall == MethodKind.CONTAINS) {
			if (parameters.get(0) instanceof String && parameters.get(1) instanceof String) {
				String valueParam1 = (String) parameters.get(0);
				String valueParam2 = (String) parameters.get(1);

				return valueParam1.contains(valueParam2);
			} else {
				throw new ODataApplicationException("Contains needs two parametes of type Edm.String",
						HttpStatusCode.BAD_REQUEST.getStatusCode(),
						Locale.ENGLISH);
			}
		} else if (methodCall == MethodKind.STARTSWITH) {
			if (parameters.get(0) instanceof String && parameters.get(1) instanceof String) {
				String valueParam1 = (String) parameters.get(0);
				String valueParam2 = (String) parameters.get(1);

				return valueParam1.startsWith(valueParam2);
			} else {
				throw new ODataApplicationException("StartsWith needs two parametes of type Edm.String",
						HttpStatusCode.BAD_REQUEST.getStatusCode(),
						Locale.ENGLISH);
			}
		} else if (methodCall == MethodKind.ENDSWITH) {
			if (parameters.get(0) instanceof String && parameters.get(1) instanceof String) {
				String valueParam1 = (String) parameters.get(0);
				String valueParam2 = (String) parameters.get(1);

				return valueParam1.endsWith(valueParam2);
			} else {
				throw new ODataApplicationException("EndsWith needs two parametes of type Edm.String",
						HttpStatusCode.BAD_REQUEST.getStatusCode(),
						Locale.ENGLISH);
			}

		} else {
			throw new ODataApplicationException("Method call " + methodCall + " not implemented",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	@Override
	public Object visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException {
		// To keep this tutorial simple, our filter expression visitor supports only
		// Edm.Int32 and Edm.String
		// In real world scenarios it can be difficult to guess the type of an literal.
		// We can be sure, that the literal is a valid OData literal because the URI
		// Parser checks
		// the lexicographical structure
		// String literals start and end with an single quotation mark
		String literalAsString = literal.getText();
		if (literal.getType() instanceof EdmString) {
			String stringLiteral = "";
			if (literal.getText().length() > 2) {
				stringLiteral = literalAsString.substring(1, literalAsString.length() - 1);
			}
			return stringLiteral;
		} else if (literal.getType() instanceof EdmDateTimeOffset) {
			return TimeUtil.convertStringToTimestamp(literalAsString);
		} else if (literal.getType() instanceof EdmGuid) {
			return UUID.fromString(literalAsString);
		} else if (literal.getType() instanceof EdmBoolean) {
			return Boolean.valueOf(literalAsString);
		} else {
			try {
				return Long.parseLong(literalAsString);
			} catch (NumberFormatException e) {
				throw new ODataApplicationException(
						"Only Edm.Int64, Edm.String, Edm.Boolean, Edm.DateTimeOffset, Edm.Guid literals are implemented",
						HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
						Locale.ENGLISH);
			}
		}
	}

	@Override
	public Object visitMember(Member member) throws ExpressionVisitException, ODataApplicationException {
		// To keeps things simple, this tutorial allows only primitive properties.
		// We have faith that the java type of Edm.Int32 is Integer

		final List<UriResource> uriResourceParts = member.getResourcePath().getUriResourceParts();

		// Make sure that the resource path of the property contains only a single
		// segment and a
		// primitive property has been addressed. We can be sure, that the property
		// exists because
		// the UriParser checks if the property has been defined in service metadata
		// document.

		if (uriResourceParts.size() == 1 && uriResourceParts.get(0) instanceof UriResourcePrimitiveProperty) {
			UriResourcePrimitiveProperty uriResourceProperty = (UriResourcePrimitiveProperty) uriResourceParts.get(0);
			return currentEntity.getProperty(uriResourceProperty.getProperty().getName()).getValue();
		} else {
			// The OData specification allows in addition complex properties and navigation
			// properties with a target cardinality 0..1 or 1.
			// This means any combination can occur e.g. Supplier/Address/City
			// -> Navigation properties Supplier
			// -> Complex Property Address
			// -> Primitive Property City
			// For such cases the resource path returns a list of UriResourceParts
			throw new ODataApplicationException("Only primitive properties are implemented in filter expressions",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	@Override
	public Object visitAlias(String aliasName) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Alias are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
				Locale.ENGLISH);
	}

	@Override
	public Object visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Type literals are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
				Locale.ENGLISH);
	}

	@Override
	public Object visitLambdaReference(String variableName) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Lambda References are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
				Locale.ENGLISH);
	}

	@Override
	public Object visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression)
			throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Enums are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
				Locale.ENGLISH);
	}

	@Override
	public Object visitEnum(EdmEnumType type, List<String> enumValues)
			throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Enums are not implemented",
				HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
				Locale.ENGLISH);
	}
}
