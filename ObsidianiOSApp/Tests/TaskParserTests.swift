import XCTest

class TaskParserTests: XCTestCase {

    func testParseTask_ValidFrontmatter_ReturnsTask() {
        let content = """
        ---
        title: My Task
        status: todo
        due: 2023-10-31
        priority: high
        ---
        # Some notes
        """

        let task = TaskParser.parseTask(filename: "task.md", content: content)
        XCTAssertNotNil(task)
        XCTAssertEqual(task?.title, "My Task")
        XCTAssertEqual(task?.status, "todo")
        XCTAssertEqual(task?.due, "2023-10-31")
        XCTAssertEqual(task?.priority, "high")
        XCTAssertEqual(task?.filename, "task.md")
    }

    func testParseTask_MissingTitle_UsesFilename() {
        let content = """
        ---
        status: in-progress
        ---
        """

        let task = TaskParser.parseTask(filename: "MyFile.md", content: content)
        XCTAssertNotNil(task)
        XCTAssertEqual(task?.title, "MyFile")
        XCTAssertEqual(task?.status, "in-progress")
    }

    func testParseTask_NoFrontmatter_ReturnsNil() {
        let content = """
        # Just a note
        No frontmatter here
        """

        let task = TaskParser.parseTask(filename: "note.md", content: content)
        XCTAssertNil(task)
    }

    func testParseTask_QuotedValues_ReturnsCleanValues() {
        let content = """
        ---
        status: "todo"
        title: 'Quoted Title'
        ---
        """

        let task = TaskParser.parseTask(filename: "test.md", content: content)
        XCTAssertNotNil(task)
        XCTAssertEqual(task?.status, "todo")
        XCTAssertEqual(task?.title, "Quoted Title")
    }
}
