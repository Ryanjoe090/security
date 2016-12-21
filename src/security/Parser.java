/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import security.Term.Type;

/**
 *
 * @author Think
 */
public class Parser {
    Pattern p;
    Matcher m;
    
    public Term parse(String masterString)
    {
        Term term;
        if(masterString.substring(0,1).equals("["))
        {
            term = parseList(masterString);
        }
        else if(masterString.substring(0,1).equals("{"))
        {
           term = parseEncryption(masterString);
        }
        else if(masterString.substring(0,1).equals("h"))
        {
           term = parseHash(masterString);
        }
        else if(masterString.substring(0,1).equals("p") || masterString.substring(0,1).equals("s"))
        {
           term = parseKey(masterString);
        }
        else
        {
           term = parseVariable(masterString);
        }
        
        return term;
    }
    
    public Term parseList(String userList)
    {
        
            Term finalList;
            String[] termsInList;
            Stack<Term> termStack = new Stack();
            userList = userList.substring(1, userList.length()-1);
            if(userList.contains("["))
            {
                //regexregexregexregexregex
                //char[] chars = userList.toCharArray();
                List<String> tempList = new LinkedList();
                int counter = 0;
               //termsInList = new String[userList.length() - userList.replace("[","").length()];
                for(int i = 0; i < userList.length(); i++)
                {
                    if(userList.charAt(i) == '[')
                    {
                        counter++;
                    }
                    else if(userList.charAt(i) == ']')
                    {
                        counter--;
                    }
                    else if(userList.charAt(i) == ',' && counter == 0)
                    {
                        tempList.add(userList.substring(0,i));
                        userList = userList.substring(i+1, userList.length());
                        i = 0;
                        //userList.
                        //tempList.
                    }
                }
                
            }
            userList = userList.replace(" ", "");
            termsInList = userList.split(",");
            for(String termS : termsInList)
            {
                Term term;
                term = parse(termS);
                termStack.push(term);
            }
            finalList = Term.createList(termStack);
            return finalList;
        
    }
    
    public Term parseEncryption(String paramTerm)
    {
        Term encryption;
        int index = paramTerm.lastIndexOf("}");
        String encryptee = paramTerm.substring(0, index);
        String key = paramTerm.substring(index+1, paramTerm.length());
        encryptee = encryptee.substring(1, encryptee.length());
        Term encrypteeTerm = parse(encryptee);
        Term keyTerm = parse(key);
        if(keyTerm.getType() == Type.PK || keyTerm.getType() == Type.SK)
        {
            encryption = new Term(paramTerm,Type.AENC, 2);
        }
        else
        {
            encryption = new Term(paramTerm,Type.SENC, 2);
        }
        encryption.setSubTerms(encrypteeTerm);
        encryption.setSubTerms(keyTerm);
        return encryption;
    }
    
    public Term parseKey(String keyString)
    {
        Term finalKey;
        
        if(keyString.contains("pk("))
        {
            String secretPair = keyString.replace("pk(", "sk(");
            Term pair = new Term(secretPair, Type.SK, 1);
            finalKey = new Term(keyString, Type.PK, 1);
            finalKey.setSubTerms(pair);
            pair.setSubTerms(finalKey);
            
        }
        else if (keyString.contains("sk("))
        {
            String publicPair = keyString.replace("sk(", "pk(");
            Term pair = new Term(publicPair, Type.PK, 1);
            finalKey = new Term(keyString, Type.SK, 1);
            finalKey.setSubTerms(pair);
            pair.setSubTerms(finalKey);
        }
        else
        {
            finalKey = new Term(keyString, Type.SYMM_KEY, 0);
        }
        
        return finalKey;
    }
    
    public Term parseHash(String hashString)
    {
        Term hash = new Term(hashString, Type.HASH, 1);
        String subTerm = hashString.substring(2, hashString.length()-1);
        Term hashee = parse(subTerm);
        hash.setSubTerms(hashee);
        return hash;
    }
    
    public Term parseVariable(String variableString)
    {
       Term variable = new Term(variableString,Type.VARIABLE, 0);
       return variable;
    }
    
}
