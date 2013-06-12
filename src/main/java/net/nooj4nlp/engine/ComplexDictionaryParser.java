package net.nooj4nlp.engine;

import java.util.ArrayList;

public class ComplexDictionaryParser {

	public static boolean parse(String line, RefObject<String> entryRef, RefObject<String[]> lexemesRef, RefObject<String[]> propertiesRef, Integer[] indices, Property[] features) throws ParsingException
    {
        // initialization
        line = line.trim();
        entryRef.argvalue = null;
        lexemesRef.argvalue = null;
        indices = null;
        propertiesRef.argvalue = null;
        features = null;
        StringBuilder _lvalue = new StringBuilder();
        ArrayList<String> lexeme1 = new ArrayList<String>();
        ArrayList<Integer> indices1 = new ArrayList<Integer>();
        // array of properties
        ArrayList<String> properties1 = new ArrayList<String>();
        // For every prop in _properties, we must know is it simple feature or name-value pair
        ArrayList<Property> features1 = new ArrayList<Property>();

        int i = 0;

        while (i < line.length() && line.charAt(i) != ',' && line.charAt(i) != '<' && line.charAt(i) != '>' && line.charAt(i) != '+')
        {
            if (line.charAt(i) == '\\')
            {
                if (i + 1 >= line.length())
                    return false;
                _lvalue.append(line.charAt(i + 1));
                i += 2;
                continue;
            }
            if (line.charAt(i) == '"')
            {
                i++;
                while (i < line.length() && line.charAt(i) != '"')
                {
                    _lvalue.append(line.charAt(i));
                    i++;
                }
            }
            else
                _lvalue.append(line.charAt(i));
            i++;
        }
        if (i == 0 || i >= line.length() || line.charAt(i) != ',')
            return false;

        entryRef.argvalue = _lvalue.toString();
        indices1.add(i);

        i++;

        while (i < line.length() && line.charAt(i) == '<')
        {
            while (i < line.length() && line.charAt(i) != '>')
            {
                if (line.charAt(i) == '"')
                {
                    i++;
                    while (i < line.length() && line.charAt(i) != '"')
                        i++;
                }
                i++;
            }
            if (i >= line.length())
                return false;
            lexeme1.add(line.substring(indices1.get(indices1.size() - 1) + 2, i ));
            indices1.add(i);
            i++;
        }
        if (i > line.length())
            return false;

        if (i == line.length())
        {
            if (lexeme1.size() == 0)
                return false;
            indices1.add(i);
            indices = indices1.toArray(new Integer[indices1.size()]);
            lexemesRef.argvalue = lexeme1.toArray(new String[lexeme1.size()]);
            propertiesRef.argvalue = properties1.toArray(new String[properties1.size()]);
            features = features1.toArray(new Property[features1.size()]);
            return true;
        }

        if (line.charAt(i++) != '+')
            return false;

        indices1.add(i - 1);

        // finite automata simulation
        int state = 8;
        _lvalue = new StringBuilder();
        for (; i < line.length(); i++)
        {
            switch (state)
            {
                case 8:
                    if (Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '_')
                    {
                        _lvalue.append(line.charAt(i));
                        state = 8;
                    }
                    else if (line.charAt(i) == '+')
                    {
                        properties1.add(_lvalue.toString());
                        _lvalue = new StringBuilder();
                        features1.add(Property.SimpleFeature);
                        indices1.add(i);
                        state = 8;
                    }
                    else if (line.charAt(i) == '=')
                    {
                        features1.add(Property.NameValuePair);
                        _lvalue.append(line.charAt(i));
                        state = 9;
                    }
                    else
                        return false;
                    break;
                case 9:
                    if (line.charAt(i) == '"')
                    {
                        if (line.charAt(i - 1) != '=')
                            throw new ParsingException("At character " + i + ":  Property value can not contain double quotes (put a backslash in front of it), or missing quotes right behind the equals sign.");
                        state = 10;
                    }
                    else if (line.charAt(i) == '+')
                    {
                        properties1.add(_lvalue.toString());
                        _lvalue = new StringBuilder();
                        indices1.add(i);
                        state = 8;
                    }
                    else if (line.charAt(i) == '\\')
                    {
                        state = -9;
                    }
                    else if (line.charAt(i) != '<' && line.charAt(i) != '>' && line.charAt(i) != ',' || line.charAt(i) != '#')
                   
                    {
                        _lvalue.append(line.charAt(i));
                        state = 9;
                    }
                    else
                        return false;
                    break;
                case -9:
                    _lvalue.append(line.charAt(i));
                    state = 9;
                    break;
                case 10:
                    if (line.charAt(i) == '"')
                    {
                        state = 11;
                    }
                    else
                    {
                        state = 10;
                        _lvalue.append(line.charAt(i));
                    }
                    break;
                case 11:
                    if (line.charAt(i) == '+')
                    {
                        properties1.add(_lvalue.toString());
                        _lvalue = new StringBuilder();
                        indices1.add(i);
                        state = 8;
                    }
                    else
                        return false;
                    break;
                default:
                    throw new ParsingException("Unknown state!");
            }
        }

        if (state == 8)
        {
            properties1.add(_lvalue.toString());
            features1.add(Property.SimpleFeature);
        }

        if (state == 9 || state == 11)
        {
            properties1.add(_lvalue.toString());
          
        }

        indices1.add(line.length());

        indices = indices1.toArray(new Integer[indices1.size()]);
        lexemesRef.argvalue = lexeme1.toArray(new String[lexeme1.size()]);
        propertiesRef.argvalue = properties1.toArray(new String[properties1.size()]);
        features = features1.toArray(new Property[features1.size()]);

        for (int j = 0; j < propertiesRef.argvalue.length; j++)
        {
            if (features[j] == Property.SimpleFeature)
            {
                if (propertiesRef.argvalue[j].equals(""))
                    return false;
            }
            else
            {
                int pos_of_eq = propertiesRef.argvalue[j].indexOf('=');
                if (pos_of_eq == 0)
                    return false;
                if (pos_of_eq == propertiesRef.argvalue[j].length() - 1)
                    return false;
            }
        }
        return true;
    }
}
