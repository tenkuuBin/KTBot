package my.ktbot.plugs

import my.ktbot.annotation.AutoSend
import my.ktbot.utils.CacheMap
import my.miraiplus.annotation.MessageHandle
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*

/**
 *  @Date:2022/1/8
 *  @author bin
 *  @version 1.0.0
 */
object CQBotRepeat {

	@JvmStatic
	private val cache = CacheMap<Long, RepeatCache>()

	@MessageHandle("(复读)", priority = EventPriority.LOW)
	@AutoSend
	fun invoke(event: GroupMessageEvent): Message? {
		val node = cache[event.group.id]
		if (node === null || node.msg != event.message.contentToString()) {
			cache[event.group.id] = RepeatCache(event.message.contentToString()).also {
				it += event.sender.id
			}
			return null
		}
		node += event.sender.id
		if (node.size != 4) return EmptyMessageChain
		val msg: String = event.message.filterIsInstance<PlainText>().joinToString("") {
			it.contentToString()
		}
		if (msg.length < 4) return msg.toPlainText()
		return msg.shuffle().toPlainText()
	}

	@JvmStatic
	private fun String.shuffle(): String {
		return String(toCharArray().apply(CharArray::shuffle))
	}

	private class RepeatCache(val msg: String) {
		val users = mutableSetOf<Long>()
		val size get() = users.size

		operator fun plusAssign(qq: Long) {
			users += qq
		}
	}
}
