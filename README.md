# ReVerb
This is a plugin for Runelite, which replaces right-click menu options (such as 'Fish' or 'Attack') with your own custom phrases.



Input format is "Menu Option:Replacement", leading and trailing whitespace is trimmed, letting the list be made more readable. Hyphens and numbers are accepted, despite the instructions (I forgot to update the text!). Pairs are separated by semicolons. I tried my best to think of problematic cases but make sure to let me know if you find something! 

Example:
*  "Eat:Consume" is the same as "        Eat   :  Consume     ;"
*  "Walk here:Move" isn't the same as "Walk       here:Move"
* More than one change: "Talk-To : Pester ; Drop : Yeet"
