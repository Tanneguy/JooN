package net.nooj4nlp.engine;

import java.util.ArrayList;

public class DictionaryParser {
    public static boolean parse(String line, RefObject<String> entryRef, RefObject<String> lemmaRef, RefObject<String> categoryRef, RefObject<String[]> propertiesRef,
    		Integer[] indices, Property[] features) throws ParsingException
    {
        // read word
        StringBuilder _lvalue = new StringBuilder();

        // initialization
        entryRef.argvalue = lemmaRef.argvalue = categoryRef.argvalue = "";
        propertiesRef.argvalue = null;
        indices = null;
        features = null;
        // array of properties
        ArrayList<String> properties1 = new ArrayList<String>();
        // array of indices needed for coloring text
        ArrayList<Integer> indices1 = new ArrayList<Integer>();
        // For every prop in _properties, we must know is it simple feature or name-value pair
        ArrayList<Property> features1 = new ArrayList<Property>();

        // if empty line or if comment
        if (line.length() == 0 || line.charAt(0) == '#')
        {
            return false;
        }

        // finite automata simulation
        int state = 1;
        _lvalue = new StringBuilder();
        for (int i = 0; i < line.length(); i++)
        {
            switch (state)
            {
                case 1:
                  
                    if (line.charAt(i) == ',')
                    {
                        entryRef.argvalue = _lvalue.toString();
                        _lvalue = new StringBuilder();
                        indices1.add(i);
                        state = 3;
                    }
                    else if (line.charAt(i) == '\\')
                    {
                        state = -1;
                    }
                    else if (line.charAt(i) != '<' && line.charAt(i) != '>' && line.charAt(i) != '#')
                    {
                        _lvalue.append(line.charAt(i));
                        state = 1;
                    }
                    else
                    {
                        throw new ParsingException("At character " + i + ": Entry may not contain character '" + line.charAt(i) + "'.");
                    }
                    break;
                case -1:
                    _lvalue.append(line.charAt(i));
                    state = 1;
                    break;
                
                case 3:
                    if (Character.isUpperCase(line.charAt(i)))
                    {
                        _lvalue.append(line.charAt(i));
                        state = 3;
                    }
                  
                    else if (line.charAt(i) == '+')
                    {
                        lemmaRef.argvalue = entryRef.argvalue;
                        categoryRef.argvalue = _lvalue.toString();
                        _lvalue = new StringBuilder();
                        indices1.add(indices1.get(indices1.size() - 1));
                        indices1.add(i);
                        state = 8;
                    }
                    else if (line.charAt(i) == ',')
                    {
                        lemmaRef.argvalue = _lvalue.toString();
                        _lvalue = new StringBuilder();
                        indices1.add(i);
                        state = 7;
                    }
                    else if (line.charAt(i) == '\\')
                    {
                        state = -4;
                    }
                    else if (line.charAt(i) != '<' && line.charAt(i) != '>' && line.charAt(i) != '#')
                    {
                        _lvalue.append(line.charAt(i));
                        state = 4;
                    }
                    else
                        throw new ParsingException("At character " + i + ": Lemma may not contain character '" + line.charAt(i) + "'.");
                    break;
                case 4:
                    if (line.charAt(i) == ',')
                    {
                        lemmaRef.argvalue = _lvalue.toString();
                        _lvalue = new StringBuilder();
                        indices1.add(i);
                        state = 7;
                    }
                   
                    else if (line.charAt(i) == '\\')
                    {
                        state = -4;
                    }
                    else if (line.charAt(i) != '<' && line.charAt(i) != '>' && line.charAt(i) != '#')
                    {
                        _lvalue.append(line.charAt(i));
                        state = 4;
                    }
                    else
                        throw new ParsingException("At character " + i + ": Lemma may not contain character '" + line.charAt(i) + "'.");
                    break;
                case -4:
                    _lvalue.append(line.charAt(i));
                    state = 4;
                    break;
               
                case 7:
                    if (Character.isUpperCase(line.charAt(i)))
                    {
                        _lvalue.append(line.charAt(i));
                        state = 7;
                    }
                    else if (line.charAt(i) == '+')
                    {
                        categoryRef.argvalue = _lvalue.toString();
                        _lvalue = new StringBuilder();
                        indices1.add(i);
                        state = 8;
                    }
                    else
                        throw new ParsingException("At character " + i + ":  Category must be in capital letters, the character '" + line.charAt(i) + "' is not allowed.");
                    break;
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
                        throw new ParsingException("At character " + i + ":  Property name and feature must contain only letters, numbers and _, the character '" + line.charAt(i) + "' is not allowed.");
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
                    else if (line.charAt(i) != '<' && line.charAt(i) != '>' && line.charAt(i) != ',' && line.charAt(i) != '#')
                    
                    {
                        _lvalue.append(line.charAt(i));
                        state = 9;
                    }
                    else
                        throw new ParsingException("At character " + i + ":  Property value can not contain character '" + line.charAt(i) + "'. Put it in quotes!");
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
                        throw new ParsingException("At character " + i + ":  After double quotes must be plus or end of line, but not character '" + line.charAt(i) + "'.");
                    break;
                default:
                    throw new ParsingException("Unknown state!");
            }
        }

        if (state < 0)
            throw new ParsingException("At character " + line.length() + ":  After sign '\\', there must be something.");

        if (state == 1 || state == 4)
            throw new ParsingException("At character " + line.length() + ":  Missing comma.");

        if (state == 2 || state == 5 || state == 10)
            throw new ParsingException("At character " + line.length() + ":  Quotes are not closed.");

        if (state == 3 || state == 7)
        {
            categoryRef.argvalue = _lvalue.toString();
        }

        if (state == 3)
        {
            lemmaRef.argvalue = entryRef.argvalue;
            indices1.add(indices1.get(indices1.size() - 1));
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

        if (entryRef.argvalue.equals(""))
            throw new ParsingException("At character " + 0 + ":  Lines can not begin with a comma.");

        if (categoryRef.argvalue.equals(""))
            throw new ParsingException("At character " + (indices1.get(1) + 1) + ":  There is no category.");

        propertiesRef.argvalue = properties1.toArray(new String[properties1.size()]);
        indices = indices1.toArray(new Integer[indices1.size()]);
        features = features1.toArray(new Property[features1.size()]);

        for (int i = 0; i < propertiesRef.argvalue.length; i++)
        {
            if (features[i] == Property.SimpleFeature)
            {
                if (propertiesRef.argvalue[i].equals(""))
                    throw new ParsingException("At character " + ((Integer)(indices1.get(i + 2) + 1)).toString() + ": Extra character '+'.");
                if (propertiesRef.argvalue[i].toUpperCase().equals("FLX"))
                    throw new ParsingException("At character " + indices1.get(i + 3).toString() + ": The FLX feature must absolutely be followed by a '=' character.");
                if (propertiesRef.argvalue[i].toUpperCase().equals("DRV"))
                    throw new ParsingException("At character " + indices1.get(i + 3).toString() + ": The DRV feature must absolutely be followed by a '=' character.");
            }
            else
            {
                int pos_of_eq = propertiesRef.argvalue[i].indexOf('=');
                if (pos_of_eq == 0)
                    throw new ParsingException("At character " + ((Integer)(indices1.get(i + 2) + 1 + pos_of_eq)).toString() + ": Missing property name.");
                if (pos_of_eq == propertiesRef.argvalue[i].length() - 1)
                    throw new ParsingException("At character " + ((Integer)(indices1.get(i + 2) + pos_of_eq + 2)).toString() + ": Missing property value.");
            }
        }
        return true;
    }
}