Description of the program:

Compile: 

Multipage: two buttons at bottom of screen, for previous page and next page. If no previous page/ next page, the corresponding will be grey, otherwise black. Page number displayed at bottom centre. Orientation is disabled. Vertical orientation only. 

Drawing/highlighting : recorded paths for each page and drawn with corresponding paint. Blue + stroke width 3 for pen and Yellow + stroke width 30 + 70 Alpha for highlighter.

Eraser: for eraser feature, I recorded the erasing path and looped through the paths recorded in pen and highlighter, then created two regions (full layout size), one with the erasing path and one with each drawn path, then used built in quickReject method to check if two regions intersect. HOWEVER, I've noticed that (RARELY) straight lines sometimes do not get erased if the user erases too fast. So for this reason, please erase slowly and avoid short, straight lines. ANY OTHER DRAWING PATH WORKS FINE. 

Undo/redo: used undo stack and redo stack for each page to store all actions (no size limit), supports drawing and eraser actions. When there's nothing to redo/undo, that button will be grey, otherwise it's purple. 

Zoom and Pan: Zoom and Pan is persistent across pages, meaning if user zoomed in on page1 and goes to page 2, page 2 will have the same zoom ratio, same applies for pan. Zoom operation looks very choppy but still works, pan operation looks smooth. Note if user is zoomed out or dragged away from the pdf, user is only allowed to annotate the pdf area, not the blank space outside of the pdf. 

Data persistency: only closes renderer in onDestroy(). Didn't save data manually, the data is already persistent when running app in emulator. Switching between apps do not cause the data to be lost, but destroying the app will lose the data. 

Enhancements of the program:



Information:
openjdk version "11.0.8" 2020-09-29
Android 11.0 (R)
macOS 10.14.6 (MacBook Pro 2017)

IDE: Android Studio 4.1.1
Build #AI-201.8743.12.41.6953283, built on November 4, 2020
Runtime version: 1.8.0_242-release-1644-b3-6915495 x86_64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
macOS 10.14.6
GC: ParNew, ConcurrentMarkSweep
Memory: 1981M
Cores: 8


Image Assets:
Pen image : http://clipart-library.com/clipart/fountain-pen-clipart_2.htm
Highlighter image : http://clipart-library.com/clipart/highlighter-cliparts_4.htm
Eraser image : http://clipart-library.com/clipart/board-eraser-cliparts_3.htm
nextPage : built in android image asset
prevPage : built in android image asset 
Undo : built in android image asset
Redo : built in android image asset
	
