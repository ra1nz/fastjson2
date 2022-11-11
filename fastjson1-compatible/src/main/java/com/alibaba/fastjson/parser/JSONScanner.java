package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson2.JSONReader;

public class JSONScanner
        extends JSONLexerBase {
    private final JSONReader reader;
    private boolean orderedField;

    private String strVal;

    public JSONScanner(JSONReader reader) {
        this.reader = reader;
    }

    public JSONScanner(String str) {
        this.reader = JSONReader.of(str);
    }

    public JSONScanner(String str, int features) {
        this.reader = JSONReader.of(str, JSON.createReadContext(features));
    }

    @Override
    public JSONReader getReader() {
        return reader;
    }

    public boolean isOrderedField() {
        return orderedField;
    }

    @Override
    public String stringVal() {
        return strVal;
    }

    public void config(Feature feature, boolean state) {
        JSONReader.Feature rawFeature = null;

        boolean not = false;
        switch (feature) {
            case AllowUnQuotedFieldNames:
                rawFeature = JSONReader.Feature.AllowUnQuotedFieldNames;
                break;
            case SupportArrayToBean:
                rawFeature = JSONReader.Feature.SupportArrayToBean;
                break;
            case DisableFieldSmartMatch:
                rawFeature = JSONReader.Feature.SupportSmartMatch;
                not = true;
                break;
            case SupportAutoType:
                rawFeature = JSONReader.Feature.SupportAutoType;
                break;
            case NonStringKeyAsString:
                rawFeature = JSONReader.Feature.NonStringKeyAsString;
                break;
            case ErrorOnEnumNotMatch:
                rawFeature = JSONReader.Feature.ErrorOnEnumNotMatch;
                break;
            case SupportClassForName:
                rawFeature = JSONReader.Feature.SupportClassForName;
                break;
            case ErrorOnNotSupportAutoType:
                rawFeature = JSONReader.Feature.ErrorOnNotSupportAutoType;
                break;
            case UseNativeJavaObject:
                rawFeature = JSONReader.Feature.UseNativeObject;
                break;
            case OrderedField:
                orderedField = state;
                break;
            default:
                break;
        }

        if (rawFeature == null) {
            return;
        }

        if (not) {
            state = !state;
        }

        JSONReader.Context context = reader.getContext();
        context.config(rawFeature, state);
    }

    @Override
    public boolean isBlankInput() {
        return reader.isEnd();
    }

    @Override
    public int intValue() {
        return reader.getInt32Value();
    }

    @Override
    public long longValue() {
        return reader.getInt64Value();
    }

    public final void nextToken() {
        strVal = null;
        char ch = reader.current();
        switch (ch) {
            case '[':
            case ']':
            case '{':
            case '}':
            case ':':
                reader.next();
                return;
            case '"':
            case '\'':
                strVal = reader.readString();
                return;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
            case '+':
                reader.readNumber();
                return;
            case 't':
            case 'f':
                reader.readBoolValue();
                return;
            case 'n':
                reader.readNull();
                return;
            default:
                break;
        }

        if (reader.nextIfNull()) {
            return;
        }

        throw new JSONException("not support operation");
    }

    @Override
    public char getCurrent() {
        return reader.current();
    }

    @Override
    public final void nextToken(int expect) {
        strVal = null;
        boolean match = true;
        switch (expect) {
            case JSONToken.COLON:
                match = reader.nextIfMatch(':');
                break;
            case JSONToken.LBRACE:
                match = reader.nextIfMatch('{');
                break;
            case JSONToken.LBRACKET:
                match = reader.nextIfMatch('[');
                break;
            case JSONToken.RBRACE:
                match = reader.nextIfMatch('}');
                break;
            case JSONToken.RBRACKET:
                match = reader.nextIfMatch(']');
                break;
            case JSONToken.SET:
                match = reader.nextIfSet();
                break;
            case JSONToken.NULL:
                match = reader.nextIfNull();
                break;
            default:
                throw new JSONException("not support operation");
        }

        if (!match) {
            throw new JSONException("not support operation");
        }
    }

    @Override
    public boolean isEOF() {
        return reader.isEnd();
    }
}
