package com.flashdeal.app.infrastructure.adapter.in.graphql.scalar;

import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GraphQL Scalar 타입 설정
 */
@Configuration
public class GraphQLScalarConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
            .scalar(decimalScalar())
            .scalar(dateTimeScalar());
    }

    /**
     * Decimal 스칼라 타입 (BigDecimal)
     */
    private GraphQLScalarType decimalScalar() {
        return GraphQLScalarType.newScalar()
            .name("Decimal")
            .description("Decimal scalar type for BigDecimal")
            .coercing(new Coercing<BigDecimal, String>() {
                @Override
                public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof BigDecimal) {
                        return ((BigDecimal) dataFetcherResult).toPlainString();
                    }
                    if (dataFetcherResult instanceof Number) {
                        return BigDecimal.valueOf(((Number) dataFetcherResult).doubleValue()).toPlainString();
                    }
                    throw new CoercingSerializeException("Cannot serialize value as Decimal: " + dataFetcherResult);
                }

                @Override
                public BigDecimal parseValue(Object input) throws CoercingParseValueException {
                    if (input instanceof String) {
                        try {
                            return new BigDecimal((String) input);
                        } catch (NumberFormatException e) {
                            throw new CoercingParseValueException("Cannot parse String value as Decimal: " + input, e);
                        }
                    }
                    if (input instanceof Number) {
                        return BigDecimal.valueOf(((Number) input).doubleValue());
                    }
                    throw new CoercingParseValueException("Cannot parse value as Decimal: " + input);
                }

                @Override
                public BigDecimal parseLiteral(Object input) throws CoercingParseLiteralException {
                    if (input instanceof StringValue) {
                        try {
                            return new BigDecimal(((StringValue) input).getValue());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException("Cannot parse StringValue as Decimal: " + input, e);
                        }
                    }
                    if (input instanceof IntValue) {
                        try {
                            return new BigDecimal(((IntValue) input).getValue());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException("Cannot parse IntValue as Decimal: " + input, e);
                        }
                    }
                    if (input instanceof FloatValue) {
                        try {
                            BigDecimal floatValue = ((FloatValue) input).getValue();
                            return floatValue;
                        } catch (Exception e) {
                            throw new CoercingParseLiteralException("Cannot parse FloatValue as Decimal: " + input, e);
                        }
                    }
                    throw new CoercingParseLiteralException("Cannot parse literal as Decimal. Expected StringValue, IntValue, or FloatValue, but got: " + (input != null ? input.getClass().getName() : "null"));
                }
            })
            .build();
    }

    /**
     * DateTime 스칼라 타입 (ZonedDateTime)
     */
    private GraphQLScalarType dateTimeScalar() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        
        return GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("DateTime scalar type for ZonedDateTime")
            .coercing(new Coercing<ZonedDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof ZonedDateTime) {
                        return ((ZonedDateTime) dataFetcherResult).format(formatter);
                    }
                    throw new CoercingSerializeException("Cannot serialize non-ZonedDateTime value as DateTime: " + dataFetcherResult);
                }

                @Override
                public ZonedDateTime parseValue(Object input) throws CoercingParseValueException {
                    if (input instanceof String) {
                        try {
                            return ZonedDateTime.parse((String) input, formatter);
                        } catch (Exception e) {
                            throw new CoercingParseValueException("Cannot parse String value as DateTime: " + input, e);
                        }
                    }
                    throw new CoercingParseValueException("Cannot parse value as DateTime: " + input);
                }

                @Override
                public ZonedDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                    if (input instanceof StringValue) {
                        try {
                            return ZonedDateTime.parse(((StringValue) input).getValue(), formatter);
                        } catch (Exception e) {
                            throw new CoercingParseLiteralException("Cannot parse StringValue as DateTime: " + input, e);
                        }
                    }
                    throw new CoercingParseLiteralException("Cannot parse literal as DateTime: " + input);
                }
            })
            .build();
    }
}

