package com.development_felber.dartapp.data.persistent.database.dart_set

class FakeDartSetDao : DartSetDao {

    private val dartSetsById: HashMap<Long, DartSet> = HashMap()

    override suspend fun insert(dartSet: DartSet) {
        if (dartSetsById.containsKey(dartSet.id)) {
            dartSet.id = dartSetsById.keys.max() + 1
        }
        dartSetsById[dartSet.id] = dartSet
    }

    override suspend fun get(id: Long): DartSet? {
        return dartSetsById[id]
    }

    override suspend fun getAll(): List<DartSet> {
        return dartSetsById.values.toList()
    }
}